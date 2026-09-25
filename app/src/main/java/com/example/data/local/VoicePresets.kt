package com.example.data.local

import com.example.data.model.BackgroundMusicTrack
import com.example.data.model.EmotionalToneOption
import com.example.data.model.VoiceOption
import com.example.data.model.YouTubeScriptTemplate

object VoicePresets {

    val EMOTIONAL_TONES = listOf(
        EmotionalToneOption(
            id = "tone_neutral",
            name = "Neutral & Confident",
            description = "Balanced, professional studio delivery with crisp clarity.",
            pitchShift = 1.0f,
            rateShift = 1.0f,
            promptDirective = "Deliver the script in a clear, confident, and professional YouTube narration style.",
            iconName = "CheckCircle"
        ),
        EmotionalToneOption(
            id = "tone_happy",
            name = "Happy & Upbeat",
            description = "Bright, positive, high-enthusiasm tone for entertaining content.",
            pitchShift = 1.06f,
            rateShift = 1.08f,
            promptDirective = "Speak with genuine excitement, warm enthusiasm, and an upbeat, smiling vocal inflection.",
            iconName = "SentimentVerySatisfied"
        ),
        EmotionalToneOption(
            id = "tone_hype",
            name = "Viral Hype & Energy",
            description = "Rapid, adrenaline-fueled pacing for YouTube Shorts, gaming & hooks.",
            pitchShift = 1.10f,
            rateShift = 1.20f,
            promptDirective = "Deliver with extreme urgency, magnetic energy, and rapid-fire punchy articulation.",
            iconName = "Bolt"
        ),
        EmotionalToneOption(
            id = "tone_dramatic",
            name = "Dramatic & Suspenseful",
            description = "Low resonance, deliberate pauses, intense storytelling tone.",
            pitchShift = 0.92f,
            rateShift = 0.92f,
            promptDirective = "Narrate with deep suspense, controlled dramatic tension, and solemn pacing.",
            iconName = "TheaterComedy"
        ),
        EmotionalToneOption(
            id = "tone_inspiring",
            name = "Inspiring & Warm",
            description = "Motivational, emotionally resonant, and empathetic delivery.",
            pitchShift = 1.02f,
            rateShift = 0.98f,
            promptDirective = "Speak warmly and upliftingly, delivering inspiration with heartfelt resonance.",
            iconName = "Favorite"
        ),
        EmotionalToneOption(
            id = "tone_whisper",
            name = "Intimate / ASMR",
            description = "Soft, close-proximity whisper without harsh plosives.",
            pitchShift = 0.96f,
            rateShift = 0.85f,
            promptDirective = "Speak in a soft, close-mic whisper tone, very calm, gentle, and relaxing.",
            iconName = "VolumeMute"
        ),
        EmotionalToneOption(
            id = "tone_sarcastic",
            name = "Sarcastic & Witty",
            description = "Dry humor, dynamic eyebrow-raising vocal inflections.",
            pitchShift = 1.0f,
            rateShift = 1.04f,
            promptDirective = "Use subtle dry wit, humorous eyebrow-raising inflections, and sarcastic punch.",
            iconName = "MoodBad"
        ),
        EmotionalToneOption(
            id = "tone_serious",
            name = "Serious & Urgent",
            description = "Direct, authoritative, critical news or investigative tone.",
            pitchShift = 0.95f,
            rateShift = 1.02f,
            promptDirective = "Deliver with utmost seriousness, journalistic authority, and firm presence.",
            iconName = "Warning"
        )
    )

    val DEFAULT_VOICES = listOf(
        // --- US ENGLISH VOICES ---
        VoiceOption(
            id = "gemini_charon",
            name = "Charon",
            gender = "Male",
            ageBracket = "Middle-Aged (38)",
            accent = "US Deep Bass",
            category = "YouTube Pro",
            description = "Deep, commanding baritone with broadcast studio warmth. Ideal for tech reviews, video essays, and documentaries.",
            geminiVoiceName = "Charon",
            localeTag = "en-US",
            defaultPitch = 0.95f,
            defaultRate = 1.0f,
            recommendedTone = "Neutral & Confident",
            timbreColorHex = 0xFF8B5CF6
        ),
        VoiceOption(
            id = "gemini_puck",
            name = "Puck",
            gender = "Male",
            ageBracket = "Youth (21)",
            accent = "US High-Energy",
            category = "Gaming/Hype",
            description = "Dynamic, punchy, fast-tempo delivery. Built for YouTube Shorts, Top 10 lists, gaming, and viral retention.",
            geminiVoiceName = "Puck",
            localeTag = "en-US",
            defaultPitch = 1.05f,
            defaultRate = 1.15f,
            recommendedTone = "Viral Hype & Energy",
            timbreColorHex = 0xFFFF3366
        ),
        VoiceOption(
            id = "gemini_kore",
            name = "Kore",
            gender = "Female",
            ageBracket = "Young Adult (28)",
            accent = "US Studio Clean",
            category = "Explainer",
            description = "Crystal clear, warm, soothing voice with remarkable intelligibility. Best for tutorials, course videos, and explainer channels.",
            geminiVoiceName = "Kore",
            localeTag = "en-US",
            defaultPitch = 1.0f,
            defaultRate = 1.0f,
            recommendedTone = "Inspiring & Warm",
            timbreColorHex = 0xFF06B6D4
        ),
        VoiceOption(
            id = "voice_us_southern",
            name = "Beau (Southern Drawl)",
            gender = "Male",
            ageBracket = "Veteran (55)",
            accent = "US Deep South",
            category = "Storytelling",
            description = "Rich, resonant Southern warmth with natural storytelling pacing. Great for folklore, historical retrospectives, and podcasts.",
            geminiVoiceName = "Charon",
            localeTag = "en-US",
            defaultPitch = 0.90f,
            defaultRate = 0.92f,
            recommendedTone = "Dramatic & Suspenseful",
            timbreColorHex = 0xFFD97706
        ),
        VoiceOption(
            id = "voice_us_young_creator",
            name = "Chloe (Creator Vibe)",
            gender = "Female",
            ageBracket = "Young Adult (23)",
            accent = "US West Coast",
            category = "YouTube Pro",
            description = "Modern, friendly, conversational YouTuber cadence. Ideal for lifestyle vlogs, unboxing, and beauty breakdowns.",
            geminiVoiceName = "Aoede",
            localeTag = "en-US",
            defaultPitch = 1.04f,
            defaultRate = 1.06f,
            recommendedTone = "Happy & Upbeat",
            timbreColorHex = 0xFFEC4899
        ),

        // --- BRITISH ENGLISH VOICES ---
        VoiceOption(
            id = "gemini_fenrir",
            name = "Fenrir",
            gender = "Male",
            ageBracket = "Veteran (58)",
            accent = "British Cinematic",
            category = "Documentary",
            description = "Authoritative, resonant, cinematic British baritone. Designed for true crime mysteries, history, and movie trailers.",
            geminiVoiceName = "Fenrir",
            localeTag = "en-GB",
            defaultPitch = 0.88f,
            defaultRate = 0.94f,
            recommendedTone = "Dramatic & Suspenseful",
            timbreColorHex = 0xFFFFB703
        ),
        VoiceOption(
            id = "gemini_aoede",
            name = "Aoede",
            gender = "Female",
            ageBracket = "Middle-Aged (35)",
            accent = "British Received (RP)",
            category = "Storytelling",
            description = "Refined, articulate storytelling tone with elegant cadence. Perfect for audiobooks, video essays, and science explainers.",
            geminiVoiceName = "Aoede",
            localeTag = "en-GB",
            defaultPitch = 1.0f,
            defaultRate = 0.98f,
            recommendedTone = "Neutral & Confident",
            timbreColorHex = 0xFF14B8A6
        ),
        VoiceOption(
            id = "voice_uk_london_urban",
            name = "Leo (London Creator)",
            gender = "Male",
            ageBracket = "Young Adult (25)",
            accent = "British Urban",
            category = "Gaming/Hype",
            description = "Crisp modern London inflection, upbeat and snappy for entertainment and pop-culture channels.",
            geminiVoiceName = "Puck",
            localeTag = "en-GB",
            defaultPitch = 1.02f,
            defaultRate = 1.10f,
            recommendedTone = "Viral Hype & Energy",
            timbreColorHex = 0xFF3B82F6
        ),

        // --- AUSTRALIAN ENGLISH VOICES ---
        VoiceOption(
            id = "voice_au_liam",
            name = "Liam (Outback / Vlog)",
            gender = "Male",
            ageBracket = "Young Adult (29)",
            accent = "Australian Natural",
            category = "Gaming/Hype",
            description = "Energetic, authentic Australian accent. Fantastic for adventure vlogs, outdoor guides, and gaming commentary.",
            geminiVoiceName = "Puck",
            localeTag = "en-AU",
            defaultPitch = 1.0f,
            defaultRate = 1.06f,
            recommendedTone = "Happy & Upbeat",
            timbreColorHex = 0xFFF97316
        ),
        VoiceOption(
            id = "voice_au_freya",
            name = "Freya (Melbourne Clean)",
            gender = "Female",
            ageBracket = "Young Adult (27)",
            accent = "Australian Urban",
            category = "YouTube Pro",
            description = "Smooth, modern Australian tone for product reviews, travel videos, and daily content.",
            geminiVoiceName = "Kore",
            localeTag = "en-AU",
            defaultPitch = 1.02f,
            defaultRate = 1.02f,
            recommendedTone = "Neutral & Confident",
            timbreColorHex = 0xFFE11D48
        ),

        // --- INDIAN ENGLISH VOICES ---
        VoiceOption(
            id = "voice_in_aarav",
            name = "Aarav (Global Tech)",
            gender = "Male",
            ageBracket = "Young Adult (30)",
            accent = "Indian Tech English",
            category = "Explainer",
            description = "Highly articulate international English with professional technical cadence for coding, tech tutorials, and finance.",
            geminiVoiceName = "Charon",
            localeTag = "en-IN",
            defaultPitch = 1.0f,
            defaultRate = 1.04f,
            recommendedTone = "Neutral & Confident",
            timbreColorHex = 0xFF6366F1
        ),
        VoiceOption(
            id = "voice_in_ananya",
            name = "Ananya (Warm Explainer)",
            gender = "Female",
            ageBracket = "Young Adult (26)",
            accent = "Indian English Melodic",
            category = "Explainer",
            description = "Friendly, polite, highly comprehensible English for educational and academic YouTube series.",
            geminiVoiceName = "Kore",
            localeTag = "en-IN",
            defaultPitch = 1.03f,
            defaultRate = 0.98f,
            recommendedTone = "Inspiring & Warm",
            timbreColorHex = 0xFF10B981
        ),

        // --- IRISH & SCOTTISH VOICES ---
        VoiceOption(
            id = "voice_ie_siobhan",
            name = "Siobhan (Celtic Mystique)",
            gender = "Female",
            ageBracket = "Middle-Aged (34)",
            accent = "Irish Dublin",
            category = "Documentary",
            description = "Lyrical Irish cadence with rich emotional depth for history, mythology, and mystery essays.",
            geminiVoiceName = "Aoede",
            localeTag = "en-IE",
            defaultPitch = 1.03f,
            defaultRate = 0.95f,
            recommendedTone = "Dramatic & Suspenseful",
            timbreColorHex = 0xFF059669
        ),
        VoiceOption(
            id = "voice_scot_calum",
            name = "Calum (Highland Baritone)",
            gender = "Male",
            ageBracket = "Middle-Aged (44)",
            accent = "Scottish Highland",
            category = "Storytelling",
            description = "Deep, rugged Scottish voice with commanding gravitas for cinematic storytelling and true crime.",
            geminiVoiceName = "Fenrir",
            localeTag = "en-GB",
            defaultPitch = 0.91f,
            defaultRate = 0.93f,
            recommendedTone = "Dramatic & Suspenseful",
            timbreColorHex = 0xFF7C3AED
        ),

        // --- CANADIAN & GLOBAL VOICES ---
        VoiceOption(
            id = "voice_ca_maya",
            name = "Maya (Pacific Clean)",
            gender = "Female",
            ageBracket = "Young Adult (24)",
            accent = "Canadian Neutral",
            category = "YouTube Pro",
            description = "Crisp, non-regional Canadian English with bright resonance for multi-audience channels.",
            geminiVoiceName = "Kore",
            localeTag = "en-CA",
            defaultPitch = 1.0f,
            defaultRate = 1.0f,
            recommendedTone = "Neutral & Confident",
            timbreColorHex = 0xFF0284C7
        ),
        VoiceOption(
            id = "voice_neutral_nova",
            name = "Nova (AI Futuristic)",
            gender = "Neutral",
            ageBracket = "Young Adult (25)",
            accent = "Global Clean",
            category = "Explainer",
            description = "Hyper-clean, futuristic neutral narrator with studio precision. Great for sci-fi, future tech, and AI channels.",
            geminiVoiceName = "Charon",
            localeTag = "en-US",
            defaultPitch = 1.02f,
            defaultRate = 1.08f,
            recommendedTone = "Neutral & Confident",
            timbreColorHex = 0xFF8B5CF6
        )
    )

    val YOUTUBE_TEMPLATES = listOf(
        YouTubeScriptTemplate(
            id = "tpl_top_5",
            title = "Top 5 List Countdown",
            category = "Fast-Paced / Countdown",
            targetWpm = 155,
            estimatedMinutes = "3:30",
            description = "High-retention structure with a 5-second hook, countdown numbers with emphasis tags, and subscriber CTA.",
            iconName = "FormatListNumbered",
            suggestedVoiceId = "gemini_puck",
            suggestedTone = "Viral Hype & Energy",
            sampleScript = """
[Excited] What if I told you that everything you knew about productivity was completely backwards?

In this video, we're breaking down the [Emphasis: Top 5 game-changing AI tools] that will save you over 10 hours every single week.

[Emphasis] Number 5: The Automated Timeline Editor.
Most editors spend hours trimming dead air. With new neural speech recognition, your rough cuts are ready in under thirty seconds.

[Emphasis] Number 4: Smart Sound Design.
[Pause 0.5s]
Imagine adding cinematic foley effects just by typing what you want to hear.

[Emphasis] Number 3: AI Voice Synthesis.
Never re-record a voiceover because of background noise again. You can fix mistakes with one click.

Drop a comment below with your favorite tool, and subscribe for weekly creator breakdowns!
            """.trimIndent()
        ),
        YouTubeScriptTemplate(
            id = "tpl_tech_review",
            title = "Tech Gadget Review",
            category = "Tech / Modern",
            targetWpm = 140,
            estimatedMinutes = "2:45",
            description = "Crisp, objective breakdown covering first impressions, hardware, real-world tests, and verdict.",
            iconName = "Devices",
            suggestedVoiceId = "gemini_charon",
            suggestedTone = "Neutral & Confident",
            sampleScript = """
I’ve been testing this device as my daily driver for the past two weeks, and frankly, my expectations were completely shattered.

[Deep Tone] Let's start with the build quality.
The [Emphasis: matte titanium chassis] feels noticeably lighter in the hand, yet significantly more durable than last year’s glass sandwich.

[Emphasis] The Performance Benchmark.
Under sustained creative workloads like 4K HDR export, thermal throttling was practically non-existent.

[Pause 0.5s]
So, who is this actually for?
If you're upgrading from a three-year-old model, the leap in battery efficiency alone makes it worth every penny.
            """.trimIndent()
        ),
        YouTubeScriptTemplate(
            id = "tpl_documentary",
            title = "Cinematic Mystery & Essay",
            category = "Documentary / Deep",
            targetWpm = 120,
            estimatedMinutes = "4:00",
            description = "Atmospheric pacing with tension build-ups, rhetorical pauses, and dramatic storytelling.",
            iconName = "AutoStories",
            suggestedVoiceId = "gemini_fenrir",
            suggestedTone = "Dramatic & Suspenseful",
            sampleScript = """
Deep beneath the frozen expanse of the Arctic shelf lies a silence older than recorded human civilization.

[Whisper] But in the winter of 1974, sonar operators recorded a rhythmic signal that should not have existed.

[Pause 1.0s]
[Deep Tone] It wasn't seismic activity. It wasn't the migration of whales.
It was a repeating acoustic frequency... [Emphasis: perfectly timed to the millisecond].

For decades, the archives remained sealed. Until now.
Join us as we uncover the forgotten expedition that ventured into the abyssal trench to find the source.
            """.trimIndent()
        ),
        YouTubeScriptTemplate(
            id = "tpl_shorts_hook",
            title = "Viral YouTube Short (60s)",
            category = "Shorts & Reels",
            targetWpm = 175,
            estimatedMinutes = "0:50",
            description = "Ultra high energy 3-second hook with zero fluff, rapid value delivery, and loopable ending.",
            iconName = "Bolt",
            suggestedVoiceId = "gemini_puck",
            suggestedTone = "Viral Hype & Energy",
            sampleScript = """
[Fast Pace] [Emphasis: Stop scrolling right now] if you want to double your video views this month!

Here is the secret algorithm trick that top YouTubers never talk about.
The first three seconds dictate [Emphasis: 80% of your retention curve]. If you start with "Hey guys, welcome back", your viewer is already gone.

[Emphasis] Instead, start directly in the middle of the action with visual motion.
Try this on your next upload and watch your click-through rate skyrocket!
            """.trimIndent()
        ),
        YouTubeScriptTemplate(
            id = "tpl_tutorial",
            title = "Step-by-Step Tutorial",
            category = "Educational",
            targetWpm = 130,
            estimatedMinutes = "3:15",
            description = "Clear enunciation, structured step transitions, and practical walk-through tips.",
            iconName = "School",
            suggestedVoiceId = "gemini_kore",
            suggestedTone = "Inspiring & Warm",
            sampleScript = """
Welcome to this step-by-step masterclass on audio mastering for YouTube.

[Emphasis: Step 1]: Noise Floor Reduction.
Always ensure your room background noise is filtered out before applying any compression.

[Pause 0.5s]
[Emphasis: Step 2]: Equalization.
Gently dip the muddy frequencies around 300 Hertz to give your vocal tone instant broadcast clarity.

[Emphasis: Step 3]: Output Limiting.
Set your true peak limiter to minus one decibel to prevent distortion when YouTube compresses your upload.

Follow these three steps, and your voiceovers will sound professional every time.
            """.trimIndent()
        )
    )

    val BACKGROUND_TRACKS = listOf(
        BackgroundMusicTrack("none", "None (Clean Voiceover)", "Acapella", "-", "Pure voice"),
        BackgroundMusicTrack("lofi_focus", "Chill Creator Lo-Fi", "Lo-Fi Beats", "85 BPM", "Relaxed & smooth"),
        BackgroundMusicTrack("tech_modern", "Cyber Future Tech", "Electronic", "120 BPM", "Innovative & dynamic"),
        BackgroundMusicTrack("cinematic_deep", "Abyssal Cinematic", "Ambient Drone", "65 BPM", "Suspenseful & dark"),
        BackgroundMusicTrack("acoustic_uplift", "Morning Inspiration", "Acoustic Folk", "98 BPM", "Warm & motivating"),
        BackgroundMusicTrack("epic_trailer", "Hero's Journey", "Orchestral", "130 BPM", "Powerful & climactic")
    )
}
