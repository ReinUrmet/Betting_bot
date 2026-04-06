# Rein-bot — Playtech Summer 2026 Bidding Bot

A Java bidding bot for the Playtech Summer 2026 internship assignment. Competes in real-time ad auctions against other bots, bidding on video advertising impressions to maximize value per ebuck spent.

## Java version
- Using java version 21

## Requirements

- Java 8 or higher
- The harness package (`harness.jar`) extracted into a working directory

## File Structure

```
working-dir/
├── harness.jar
├── dumb0/
├── dumb-retro/
├── silly-gpt/
├── run.sh
└── Rein-bot/
    ├── Main.java
    ├── Rein-bot.jar
    └── README.md
```

## How to Run

1. Place the `Rein-bot` folder inside the harness working directory
2. From the working directory, run:

```bash
cd Rein-bot
chmod +x run.sh   # only needed once
./run.sh
```

This will compile the bot, package it into a jar, and launch the harness automatically.

3. Open `http://localhost:2026` in your browser to see the live dashboard
4. Check `Rein-bot/err.log` after a run for logs and debug output

## How it Works

The bot competes in a simulated ad auction. Each round it receives information about a video and a viewer, scores the impression, and sends a bid.

**Category:** DIY — chosen for its engaged, high-spending audience demographic.

**Scoring** — each impression is scored based on:
- Whether the viewer's interests match DIY (ordered by relevance)
- Whether the video category matches DIY
- Viewer age — 25-54 weighted highest as most likely to purchase
- Viewer gender — slight bonus for male viewers (DIY skews male)
- Viewer subscription status — subscribed viewers are worth more
- Engagement ratio (comments/views) — higher engagement = more valuable audience

**Bidding** — bids are scaled proportionally to the score. Low scoring impressions (below threshold) are skipped entirely to preserve budget for high-value opportunities.

## Adjusting the Bot

Key constants at the top of `Main.java`:

| Constant | What it does |
|---|---|
| `CATEGORY` | The ad category to bid on |
| `TEST_MODE` | Set to `true` to run local tests against `test.txt` |

The bid multiplier and score threshold are in `makingBets()` and can be tuned to adjust how aggressively the bot spends its budget.
