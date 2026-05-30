package com.example.roast

enum class RoastLevel(val displayName: String, val emoji: String, val description: String) {
    MILD("Mild Buzz", "🌶️", "Slightly crispy, safe for work."),
    SAVAGE("Savage Burn", "🌶️🌶️", "Considerable emotional damage."),
    OBLITERATION("Obliteration", "🌶️🌶️🌶️", "Cancel-level destruction. Bring tissues.")
}

enum class RoastArchetype(val id: String, val displayName: String, val icon: String, val bio: String) {
    PROGRAMMER(
        id = "programmer",
        displayName = "Programmer",
        icon = "💻",
        bio = "Converts caffeine into bugs and light-mode arguments."
    ),
    GAMER(
        id = "gamer",
        displayName = "Gamer",
        icon = "🎮",
        bio = "RGB lights user who blames lag for 0% win-rate."
    ),
    INFLUENCER(
        id = "influencer",
        displayName = "Influencer",
        icon = "🥑",
        bio = "Manifesting cafe lattes and chasing bot followers."
    ),
    CRYPTO_BRO(
        id = "crypto",
        displayName = "Crypto Bro",
        icon = "📈",
        bio = "Buy the dip advisor living in parent's garage."
    ),
    GYM_JUNKIE(
        id = "gym",
        displayName = "Gym Junkie",
        icon = "🏋️‍♂️",
        bio = "Pre-workout addict who skips legs and macro-counts air."
    ),
    STUDENT(
        id = "student",
        displayName = "Student",
        icon = "📚",
        bio = "Procrastinates writing 5-page essays with 5-hour naps."
    ),
    REGULAR_JOE(
        id = "regular",
        displayName = "Regular NPC",
        icon = "🤡",
        bio = "Basic baseline human. Spiciness level of a raw potato."
    )
}

data class Roast(
    val text: String,
    val level: RoastLevel,
    val archetype: RoastArchetype
)

object RoastRepository {
    private val roasts = listOf(
        // --- Programmer Mild ---
        Roast(
            "You spend 3 hours writing a script to automate a 10-second task and call it 'high-IQ leverage'.",
            RoastLevel.MILD, RoastArchetype.PROGRAMMER
        ),
        Roast(
            "You have 47 browser tabs open, and 45 of them are StackOverflow answers you didn't understand.",
            RoastLevel.MILD, RoastArchetype.PROGRAMMER
        ),
        Roast(
            "Your keyboard has custom keycaps and makes clicky noises that annoy everyone within a 2-mile radius.",
            RoastLevel.MILD, RoastArchetype.PROGRAMMER
        ),

        // --- Programmer Savage ---
        Roast(
            "Your commit history has more 'fixed typo' messages than actual code, and your variables are named 'temp1', 'temp2', and 'temp_final_v2'.",
            RoastLevel.SAVAGE, RoastArchetype.PROGRAMMER
        ),
        Roast(
            "You brag about using Vim, but we all know you had to restart your computer the first time you tried to exit it.",
            RoastLevel.SAVAGE, RoastArchetype.PROGRAMMER
        ),
        Roast(
            "Your code structure has so many nested IF-statements that it looks like a geological survey of the Grand Canyon.",
            RoastLevel.SAVAGE, RoastArchetype.PROGRAMMER
        ),

        // --- Programmer Obliteration ---
        Roast(
            "You speak 5 programming languages but can't talk to a fast-food cashier without getting sweaty palms. Your real-life relationships are like your code: undocumented and prone to sudden termination.",
            RoastLevel.OBLITERATION, RoastArchetype.PROGRAMMER
        ),
        Roast(
            "You use light mode because you want to feel something, and your eyes are more bloodshot than a vampire on a diet. Your entire career is built on copy-pasting code written by a teenager in 2012, and you know it.",
            RoastLevel.OBLITERATION, RoastArchetype.PROGRAMMER
        ),

        // --- Gamer Mild ---
        Roast(
            "You think purchasing a $200 RGB gaming mouse is going to rescue you from being hard-stuck in Bronze tier.",
            RoastLevel.MILD, RoastArchetype.GAMER
        ),
        Roast(
            "You play on 'Easy' mode and still search YouTube for walk-through guides on how to clear the tutorial level.",
            RoastLevel.MILD, RoastArchetype.GAMER
        ),
        Roast(
            "You've logged 2,000 hours in a simulation game just to wash virtual trucks and avoid your real-life chores.",
            RoastLevel.MILD, RoastArchetype.GAMER
        ),

        // --- Gamer Savage ---
        Roast(
            "Your gaming chair cost more than your monthly rent, yet your win rate is lower than your room temperature in Celsius.",
            RoastLevel.SAVAGE, RoastArchetype.GAMER
        ),
        Roast(
            "You keep calling other players 'trash' as if you're not the primary garbage truck dragging your team's survival down.",
            RoastLevel.SAVAGE, RoastArchetype.GAMER
        ),

        // --- Gamer Obliteration ---
        Roast(
            "You blame 'tick rate' and 'WiFi lag' for your misses, but you're actually just the lag in your family's happiness. Your stream has exactly two concurrent viewers, and one of them is your own dashboard tab.",
            RoastLevel.OBLITERATION, RoastArchetype.GAMER
        ),
        Roast(
            "The only exercise you get is raging on Discord at 3 AM. If hand-eye coordination on a plastic controller could pay taxes, you'd still be bankrupt because your skills are completely imaginary.",
            RoastLevel.OBLITERATION, RoastArchetype.GAMER
        ),

        // --- Influencer Mild ---
        Roast(
            "You write a deep 500-word essay about 'mindfulness and inner peace' under a selfie where you're just eyeing your own reflection.",
            RoastLevel.MILD, RoastArchetype.INFLUENCER
        ),
        Roast(
            "You take 40 minutes photographing a cold avocado toast from 12 angles before eating it with a sad face.",
            RoastLevel.MILD, RoastArchetype.INFLUENCER
        ),

        // --- Influencer Savage ---
        Roast(
            "You use the word 'authentic' to describe a lifestyle completely sponsored by fast-fashion brands and face-smoothing filters.",
            RoastLevel.SAVAGE, RoastArchetype.INFLUENCER
        ),
        Roast(
            "Your daily vlog is 15 minutes of you saying 'so many of you asked about my routine' when literally nobody asked.",
            RoastLevel.SAVAGE, RoastArchetype.INFLUENCER
        ),

        // --- Influencer Obliteration ---
        Roast(
            "Your follower list is 95% inactive bot accounts and 5% family members who feel too guilty to unfollow you. If the internet went down for 24 hours, you would evaporate because you have no real-world personality.",
            RoastLevel.OBLITERATION, RoastArchetype.INFLUENCER
        ),
        Roast(
            "You live for clout and take aesthetic videos in public aisles while people are just trying to buy toilet paper. The saddest part is your monetization plan is receiving free mugs from brands you've never heard of.",
            RoastLevel.OBLITERATION, RoastArchetype.INFLUENCER
        ),

        // --- Crypto Bro Mild ---
        Roast(
            "You try to explain the blockchain and smart contracts to anyone who makes eye contact for longer than 1.5 seconds.",
            RoastLevel.MILD, RoastArchetype.CRYPTO_BRO
        ),
        Roast(
            "You have a digital picture of an ugly pixelated cat as your profile photo and tell everyone it's a 'critical asset'.",
            RoastLevel.MILD, RoastArchetype.CRYPTO_BRO
        ),

        // --- Crypto Bro Savage ---
        Roast(
            "Your portfolio is down 92% this fiscal quarter, but you're still typing 'BUY THE DIP 🚀' from your childhood twin-size bed.",
            RoastLevel.SAVAGE, RoastArchetype.CRYPTO_BRO
        ),
        Roast(
            "You call yourself a 'Web3 Visionary' but your entire net worth relies on random meme tokens with names of dogs.",
            RoastLevel.SAVAGE, RoastArchetype.CRYPTO_BRO
        ),

        // --- Crypto Bro Obliteration ---
        Roast(
            "You spent your actual life savings on a JPEG of a bored cartoon animal and convinced yourself it's a retirement plan. You get your financial advice from anonymous Twitter usernames with laser eyes, and look at you now.",
            RoastLevel.OBLITERATION, RoastArchetype.CRYPTO_BRO
        ),
        Roast(
            "You speak in acronyms like HODL, DYOR, and FOMO because your brain doesn't have the bandwidth to formulate actual financial thoughts. Your future is as decentralized and non-existent as your real profit margins.",
            RoastLevel.OBLITERATION, RoastArchetype.CRYPTO_BRO
        ),

        // --- Gym Junkie Mild ---
        Roast(
            "You spend more time rearranging the weight plates and wiping mirrors for locker-room selfies than actually doing sets.",
            RoastLevel.MILD, RoastArchetype.GYM_JUNKIE
        ),
        Roast(
            "You talk about 'macros' so incessantly that your friends think they're having lunch with a Microsoft Excel spreadsheet.",
            RoastLevel.MILD, RoastArchetype.GYM_JUNKIE
        ),

        // --- Gym Junkie Savage ---
        Roast(
            "Every single time you skip leg day, a small puppy somewhere sheds a tear. Your upper body looks like a Dorito, but your calves look like toothpicks.",
            RoastLevel.SAVAGE, RoastArchetype.GYM_JUNKIE
        ),
        Roast(
            "Your kitchen is full of plastic shaker cups, but you still can't mix a single shake without leaving chalky lumps of powder in the bottom.",
            RoastLevel.SAVAGE, RoastArchetype.GYM_JUNKIE
        ),

        // --- Gym Junkie Obliteration ---
        Roast(
            "You swallow pre-workout dry just to sit in your car for 40 minutes reading TikTok comments. Your shoulders might be broad, but your entire intellectual reserve is smaller than a single scoop of unflavored creatine.",
            RoastLevel.OBLITERATION, RoastArchetype.GYM_JUNKIE
        ),
        Roast(
            "Your blood is 70% whey protein isolate and 30% self-delusion. You grunt louder lifting 20-pound dumbbells than an engine lifting a cargo container, yet you still look like you just started yesterday.",
            RoastLevel.OBLITERATION, RoastArchetype.GYM_JUNKIE
        ),

        // --- Student Mild ---
        Roast(
            "You have 18 browser windows open for 'research', but the only one you've looked at for 3 hours is a Minecraft speedrun.",
            RoastLevel.MILD, RoastArchetype.STUDENT
        ),
        Roast(
            "Your laptop keyboard is coated in a thin layer of cheese puff crumbs and unearned confidence.",
            RoastLevel.MILD, RoastArchetype.STUDENT
        ),

        // --- Student Savage ---
        Roast(
            "Your master study routine is 5 minutes of intensive reading, followed by a 3-hour 'deserved reward nap', ending in panic.",
            RoastLevel.SAVAGE, RoastArchetype.STUDENT
        ),
        Roast(
            "You spent three hours tweaking the margin spaces and line spacing in MS Word just to get your essay to seem long enough.",
            RoastLevel.SAVAGE, RoastArchetype.STUDENT
        ),

        // --- Student Obliteration ---
        Roast(
            "You're paying thousands of dollars in tuition just to sleep through lectures and take multiple choice tests with 'C' as your default guess. Your GPA has been in 'safe mode' since the first week of class.",
            RoastLevel.OBLITERATION, RoastArchetype.STUDENT
        ),
        Roast(
            "Your parents think you're studying deep science, but your actual academic peak is finding the exact timestamp to copy solutions from a YouTube Indian educator's 240p tutorial. You are a professional procrastinator.",
            RoastLevel.OBLITERATION, RoastArchetype.STUDENT
        ),

        // --- Regular Joe Mild ---
        Roast(
            "You find oatmeal extremely adventurous, and going to bed safely at 9:30 PM on Friday is your prime weekend highlight.",
            RoastLevel.MILD, RoastArchetype.REGULAR_JOE
        ),
        Roast(
            "Your wardrobe is 10 shades of gray and black, and your favorite ice cream flavor is 'Vanilla, but without the bean specks'.",
            RoastLevel.MILD, RoastArchetype.REGULAR_JOE
        ),

        // --- Regular Joe Savage ---
        Roast(
            "If you were a culinary seasoning, you'd be plain all-purpose white flour. If you were a paint swatch, you'd be name 'Mild Oatmeal'.",
            RoastLevel.SAVAGE, RoastArchetype.REGULAR_JOE
        ),
        Roast(
            "You look like the placeholder model that is loaded in 3D games before the actual character skin is downloaded.",
            RoastLevel.SAVAGE, RoastArchetype.REGULAR_JOE
        ),

        // --- Regular Joe Obliteration ---
        Roast(
            "You are the literal baseline NPC that game developers forgot to give any lines or quest items to. Your most daring decision in the past year was choosing 2% reduced-fat milk instead of skim milk.",
            RoastLevel.OBLITERATION, RoastArchetype.REGULAR_JOE
        ),
        Roast(
            "Your life is so predictable that Google Maps suggests your commutes before you even know you're leaving. You live in a simulation, and even the simulation is bored of simulating you.",
            RoastLevel.OBLITERATION, RoastArchetype.REGULAR_JOE
        )
    )

    fun getRandomRoast(archetype: RoastArchetype, level: RoastLevel): Roast {
        val filtered = roasts.filter { it.archetype == archetype && it.level == level }
        return if (filtered.isNotEmpty()) {
            filtered.random()
        } else {
            // Safe fallback
            Roast(
                "You are so unique that our prebuilt database didn't have a roast for you. Truly, a glitch in the roasting matrix.",
                level, archetype
            )
        }
    }
}
