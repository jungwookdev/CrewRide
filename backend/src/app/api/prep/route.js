import { PrismaClient } from '@prisma/client';
import ollama from 'ollama';
const NEWS_API_KEY = process.env.NEWS_API_KEY;
import { spawn } from 'child_process';

const prisma = new PrismaClient();

// Query NewsAPI.org for each preference and return combined headlines + descriptions.
async function getRelevantNews(preferences) {
  if (!NEWS_API_KEY) {
    console.warn('No NEWS_API_KEY set');
    return '';
  }
  const uniquePrefs = [...new Set(preferences)];
  const snippets = [];
  for (const pref of uniquePrefs) {
    try {
      const url = new URL('https://newsapi.org/v2/everything');
      url.search = new URLSearchParams({
        q: pref,
        language: 'en',
        sortBy: 'publishedAt',
        pageSize: '10',
        apiKey: NEWS_API_KEY
      }).toString();
      const res = await fetch(url.toString());
      if (!res.ok) continue;
      const data = await res.json();
      data.articles.forEach(article => {
        const desc = article.description || '';
        snippets.push(`${article.title}: ${desc}`);
      });
    } catch (e) {
      console.error(`Error fetching news for ${pref}:`, e);
    }
  }

  const news_snippets = snippets.map((snippet, index) => `${index + 1}. ${snippet}`).join('\n');
  return news_snippets
}

async function generateSmallTalk(roleInstruction, context, prompt) {
  const combinedSystemMsg = `${roleInstruction}\n\n${context}`;
  
  const result = await ollama.chat({
    model: 'llama3.2',
    messages: [
      { role: 'system', content: combinedSystemMsg },
      { role: 'user', content: prompt }
    ],
    format: 'json',
    stream: false
  });

  return result.message?.content || result.response || result;
}

export async function GET(request) {
  try {
    // Determine server origin
    const url = new URL(request.url).origin;
    const { searchParams } = new URL(request.url);
    const rideId = searchParams.get('rideId');
    if (!rideId) {
      return new Response(JSON.stringify({ error: 'Missing rideId parameter' }), { status: 400 });
    }
    const employeeId = searchParams.get('employeeId');
    if (!employeeId) {
      return new Response(
        JSON.stringify({ error: 'Missing employeeId parameter' }),
        { status: 400 }
      );
    }

    const currentEmployee = await prisma.employee.findUnique({
      where: { employeeId }
    });
    const currentEmployeeName = currentEmployee?.name || employeeId;

    // Retrieve ride with driver and participant info
    const ride = await prisma.offeredRide.findUnique({
      where: { rideId },
      include: {
        driver: true,
        rideParticipants: { include: { employee: true } }
      }
    });
    if (!ride) {
      return new Response(JSON.stringify({ error: 'Ride not found' }), { status: 404 });
    }

    // Construct context narrative combining talk preferences
    let context = "";

    if (ride.driver.employeeId !== employeeId) {
      context +=
        `${ride.driver.name} is generally interested in ${ride.driver.talkPreferences}. `;
    }
    ride.rideParticipants.forEach(rp => {
      if (rp.employee.employeeId !== employeeId && rp.employee.talkPreferences) {
        context +=
          `${rp.employee.name} is generally interested in ${rp.employee.talkPreferences}. `;
      }
    });

    // Gather talk preferences excluding current user
    const preferences = [];
    if (ride.driver.employeeId !== employeeId && ride.driver.talkPreferences) {
      preferences.push(ride.driver.talkPreferences);
    }
    ride.rideParticipants.forEach(rp => {
      if (rp.employee.employeeId !== employeeId && rp.employee.talkPreferences) {
        preferences.push(rp.employee.talkPreferences);
      }
    });

    const news = await getRelevantNews(preferences);

    if (news) {
      context += `Relevant news snippets based on preferences: ${news}. `;
    }

    // Define role instruction for the LLM
    const roleInstruction =
      "You are an AI assistant helping carpool colleagues spark light, casual conversation during their commute.";

    // Define instruction for the LLM
    const prompt = `Suggest five small-talk topics that would help make ${currentEmployeeName}'s carpool more enjoyable, based on participants' interests and recent news.

      ### Return Format:
      Respond in the following JSON format:
      {
        "topics": [
          {
            "title": "Topic Title 1",
            "summary": "..."
          },
          {
            "title": "Topic Title 2",
            "summary": "..."
          },
          {
            "title": "Topic Title 3",
            "summary": "..."
          }
        ]
      }

      Only return the JSON response and nothing else.`;

    // Generate the small-talk suggestion
    const prepSuggestion = await generateSmallTalk(
      roleInstruction,
      context,
      prompt
    );

    const names = [ride.driver.name, ...ride.rideParticipants.map(rp => rp.employee.name)]
      .filter(name => name && name !== currentEmployeeName)
      .join(", ");
    const note = `These small-talk suggestions were generated based on the NPR latest news and the preferences of ${names}.`;
    console.debug(JSON.stringify({ "Note": note, prepSuggestion: prepSuggestion}))
    return new Response(JSON.stringify({ "Note": note, prepSuggestion: prepSuggestion}), {
      status: 200,
      headers: { 'Content-Type': 'application/json' }
    });
  } catch (error) {
    console.error('Error in /api/rides/prep:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}
