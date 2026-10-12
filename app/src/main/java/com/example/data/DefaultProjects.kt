package com.example.data

object DefaultProjects {

    val SAMPLE_WEB_HTML = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CYBER STRIKE — Esports Tournament Arena</title>
    <link rel="stylesheet" href="style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@500;700;900&family=Rajdhani:wght@500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <header class="navbar">
        <div class="logo">
            <span class="glow-text">CYBER</span><span>STRIKE</span>
            <div class="badge">MAXY AI BUILT</div>
        </div>
        <nav class="nav-links">
            <a href="#tournaments">Tournaments</a>
            <a href="#leaderboard">Leaderboard</a>
            <a href="#prizes">Prizes</a>
        </nav>
        <button class="btn-neon" onclick="openLoginModal()">PILOT LOGIN</button>
    </header>

    <main>
        <section class="hero">
            <div class="hero-content">
                <span class="tag">SEASON 04 ACTIVE</span>
                <h1>DOMINATE THE BATTLEGROUND</h1>
                <p>Register your squad, compete in high-stakes esports tournaments, and win exclusive verified cash rewards.</p>
                <div class="hero-cta">
                    <button class="btn-primary" onclick="alert('Registration opening in 2 hours!')">JOIN BATTLE NOW</button>
                    <button class="btn-secondary" onclick="scrollToSection('tournaments')">VIEW LOBBIES</button>
                </div>
            </div>
            <div class="hero-orb">
                <div class="arc-ring ring-1"></div>
                <div class="arc-ring ring-2"></div>
                <div class="reactor-core"></div>
            </div>
        </section>

        <section id="tournaments" class="section">
            <h2 class="section-title">ACTIVE TOURNAMENTS</h2>
            <div class="cards-grid">
                <div class="card">
                    <div class="card-tag">BGMI BATTLE ROYALE</div>
                    <h3>GRAND FINALS: TITANS CLASH</h3>
                    <p class="prize">Prize Pool: ₹1,50,000</p>
                    <div class="meta"><span>Squad: 4 Players</span><span>Slots: 18/25 Left</span></div>
                    <button class="card-btn" onclick="registerTournament('BGMI Titans')">REGISTER SQUAD</button>
                </div>
                <div class="card hot">
                    <div class="card-tag">FREE FIRE MAX</div>
                    <h3>SURVIVOR CHAMPIONSHIP</h3>
                    <p class="prize">Prize Pool: ₹75,000</p>
                    <div class="meta"><span>Duo / Squad</span><span>Slots: 4/30 Left (HOT)</span></div>
                    <button class="card-btn" onclick="registerTournament('Free Fire Survivor')">REGISTER SQUAD</button>
                </div>
                <div class="card">
                    <div class="card-tag">VALORANT CLUTCH</div>
                    <h3>NEON PROTOCOL INVITATIONAL</h3>
                    <p class="prize">Prize Pool: ₹2,00,000</p>
                    <div class="meta"><span>5v5 Competitive</span><span>Slots: 8/16 Left</span></div>
                    <button class="card-btn" onclick="registerTournament('Valorant Invitational')">REGISTER SQUAD</button>
                </div>
            </div>
        </section>
    </main>

    <!-- Interactive Login Modal -->
    <div id="loginModal" class="modal">
        <div class="modal-content">
            <span class="close-btn" onclick="closeLoginModal()">&times;</span>
            <h3 class="glow-text">PILOT AUTHENTICATION</h3>
            <p>Access your tournament roster and payouts</p>
            <input type="text" id="username" placeholder="Gamertag or Email">
            <input type="password" id="userpass" placeholder="Security Token / Password">
            <button class="btn-primary" onclick="handleLogin()">AUTHENTICATE</button>
            <p id="loginStatus" class="status-msg"></p>
        </div>
    </div>

    <script src="script.js"></script>
</body>
</html>
    """.trimIndent()

    val SAMPLE_WEB_CSS = """
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Rajdhani', sans-serif;
}

body {
    background: #050811;
    color: #E2F1FF;
    overflow-x: hidden;
}

.navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 18px 32px;
    background: rgba(10, 18, 36, 0.85);
    backdrop-filter: blur(12px);
    border-bottom: 1px solid rgba(0, 229, 255, 0.2);
    position: sticky;
    top: 0;
    z-index: 100;
}

.logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: 'Orbitron', sans-serif;
    font-weight: 900;
    font-size: 20px;
    letter-spacing: 2px;
}

.glow-text {
    color: #00E5FF;
    text-shadow: 0 0 14px rgba(0, 229, 255, 0.8);
}

.badge {
    font-size: 10px;
    background: #00FF9D;
    color: #050811;
    padding: 2px 6px;
    border-radius: 4px;
    font-weight: bold;
    letter-spacing: 1px;
}

.nav-links a {
    color: #88A0C2;
    text-decoration: none;
    margin: 0 14px;
    font-weight: 600;
    font-size: 15px;
    transition: color 0.3s;
}

.nav-links a:hover {
    color: #00E5FF;
}

.btn-neon {
    background: transparent;
    border: 1px solid #00E5FF;
    color: #00E5FF;
    padding: 8px 18px;
    font-weight: 700;
    cursor: pointer;
    box-shadow: 0 0 10px rgba(0, 229, 255, 0.3);
    transition: all 0.3s;
}

.btn-neon:hover {
    background: #00E5FF;
    color: #050811;
    box-shadow: 0 0 20px rgba(0, 229, 255, 0.8);
}

.hero {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 60px 40px;
    gap: 30px;
    background: radial-gradient(circle at 70% 30%, rgba(0, 229, 255, 0.08), transparent 60%);
}

.hero-content {
    max-width: 580px;
}

.tag {
    color: #00FF9D;
    font-weight: 700;
    letter-spacing: 2px;
    font-size: 13px;
    display: inline-block;
    margin-bottom: 12px;
}

.hero-content h1 {
    font-family: 'Orbitron', sans-serif;
    font-size: 38px;
    line-height: 1.15;
    margin-bottom: 16px;
    color: #FFFFFF;
}

.hero-content p {
    color: #88A0C2;
    font-size: 17px;
    line-height: 1.5;
    margin-bottom: 24px;
}

.hero-cta {
    display: flex;
    gap: 16px;
}

.btn-primary {
    background: #00E5FF;
    color: #050811;
    border: none;
    padding: 12px 24px;
    font-weight: 700;
    font-size: 15px;
    cursor: pointer;
    box-shadow: 0 0 18px rgba(0, 229, 255, 0.5);
    transition: transform 0.2s, box-shadow 0.2s;
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 0 28px rgba(0, 229, 255, 0.9);
}

.btn-secondary {
    background: rgba(16, 30, 58, 0.8);
    color: #E2F1FF;
    border: 1px solid rgba(255, 255, 255, 0.15);
    padding: 12px 20px;
    font-weight: 700;
    cursor: pointer;
}

.hero-orb {
    position: relative;
    width: 220px;
    height: 220px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.arc-ring {
    position: absolute;
    border-radius: 50%;
    border: 2px dashed #00E5FF;
    animation: spin 16s linear infinite;
}

.ring-1 { width: 200px; height: 200px; opacity: 0.6; }
.ring-2 { width: 150px; height: 150px; border-color: #00FF9D; animation-direction: reverse; animation-duration: 10s; }

.reactor-core {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: radial-gradient(circle, #FFFFFF 10%, #00E5FF 70%, transparent 100%);
    box-shadow: 0 0 30px #00E5FF;
}

@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

.section {
    padding: 40px 32px;
}

.section-title {
    font-family: 'Orbitron', sans-serif;
    font-size: 24px;
    margin-bottom: 24px;
    letter-spacing: 1px;
}

.cards-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 20px;
}

.card {
    background: #0A1224;
    border: 1px solid rgba(0, 229, 255, 0.2);
    padding: 24px;
    border-radius: 4px;
    transition: transform 0.3s, border-color 0.3s;
}

.card:hover {
    transform: translateY(-4px);
    border-color: #00E5FF;
}

.card.hot {
    border-color: #FF2A6D;
}

.card-tag {
    font-size: 11px;
    font-weight: 700;
    color: #00E5FF;
    margin-bottom: 8px;
}

.card h3 {
    font-family: 'Orbitron', sans-serif;
    font-size: 18px;
    margin-bottom: 10px;
}

.prize {
    color: #00FF9D;
    font-weight: 700;
    font-size: 17px;
    margin-bottom: 12px;
}

.meta {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    color: #88A0C2;
    margin-bottom: 18px;
}

.card-btn {
    width: 100%;
    padding: 10px;
    background: transparent;
    border: 1px solid #00E5FF;
    color: #00E5FF;
    font-weight: 700;
    cursor: pointer;
}

.card-btn:hover {
    background: #00E5FF;
    color: #050811;
}

/* Modal */
.modal {
    display: none;
    position: fixed;
    top: 0; left: 0; width: 100%; height: 100%;
    background: rgba(5, 8, 17, 0.85);
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: #0A1224;
    border: 1px solid #00E5FF;
    padding: 32px;
    max-width: 360px;
    width: 90%;
    position: relative;
    box-shadow: 0 0 30px rgba(0, 229, 255, 0.3);
}

.close-btn {
    position: absolute;
    top: 12px; right: 16px;
    color: #88A0C2;
    font-size: 24px;
    cursor: pointer;
}

.modal-content input {
    width: 100%;
    padding: 10px;
    margin: 10px 0;
    background: #050811;
    border: 1px solid #1E3355;
    color: #E2F1FF;
}

.status-msg {
    margin-top: 12px;
    font-size: 14px;
    text-align: center;
}
    """.trimIndent()

    val SAMPLE_WEB_JS = """
function openLoginModal() {
    document.getElementById('loginModal').style.display = 'flex';
}

function closeLoginModal() {
    document.getElementById('loginModal').style.display = 'none';
}

function handleLogin() {
    const user = document.getElementById('username').value;
    const status = document.getElementById('loginStatus');
    if (!user) {
        status.style.color = '#FF2A6D';
        status.innerText = 'Please enter gamertag!';
        return;
    }
    status.style.color = '#00FF9D';
    status.innerText = 'Welcome back Pilot ' + user + '! Access Granted.';
    setTimeout(() => {
        closeLoginModal();
    }, 1200);
}

function registerTournament(name) {
    alert('MAXY AI Protocol: Registered squad for ' + name + '! Good luck warrior.');
}

function scrollToSection(id) {
    document.getElementById(id).scrollIntoView({ behavior: 'smooth' });
}
    """.trimIndent()

    // Structured Android Project Files
    val SAMPLE_APP_FILES_JSON = """
{
  "AndroidManifest.xml": "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\">\n    <uses-permission android:name=\"android.permission.INTERNET\"/>\n    <application\n        android:allowBackup=\"true\"\n        android:label=\"Apex Tournament\"\n        android:theme=\"@style/Theme.Material3.Dark\">\n        <activity\n            android:name=\".MainActivity\"\n            android:exported=\"true\">\n            <intent-filter>\n                <action android:name=\"android.intent.action.MAIN\" />\n                <category android:name=\"android.intent.category.LAUNCHER\" />\n            </intent-filter>\n        </activity>\n    </application>\n</manifest>",
  "build.gradle.kts": "plugins {\n    alias(libs.plugins.android.application)\n    alias(libs.plugins.kotlin.compose)\n}\nandroid {\n    namespace = \"com.maxy.tournament\"\n    compileSdk = 35\n    defaultConfig {\n        applicationId = \"com.maxy.tournament\"\n        minSdk = 24\n        targetSdk = 35\n        versionCode = 1\n        versionName = \"1.0\"\n    }\n    buildFeatures { compose = true }\n}\ndependencies {\n    implementation(platform(libs.androidx.compose.bom))\n    implementation(libs.androidx.compose.material3)\n    implementation(libs.androidx.activity.compose)\n    implementation(libs.androidx.navigation.compose)\n}",
  "MainActivity.kt": "package com.maxy.tournament\n\nimport android.os.Bundle\nimport androidx.activity.ComponentActivity\nimport androidx.activity.compose.setContent\nimport androidx.compose.foundation.layout.*\nimport androidx.compose.material3.*\nimport androidx.compose.runtime.*\nimport androidx.compose.ui.Modifier\nimport com.maxy.tournament.ui.TournamentNavHost\n\nclass MainActivity : ComponentActivity() {\n    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)\n        setContent {\n            MaterialTheme(colorScheme = darkColorScheme()) {\n                Surface(modifier = Modifier.fillMaxSize()) {\n                    TournamentNavHost()\n                }\n            }\n        }\n    }\n}",
  "ui/TournamentNavHost.kt": "package com.maxy.tournament.ui\n\nimport androidx.compose.runtime.*\nimport androidx.compose.material3.*\nimport androidx.compose.foundation.layout.*\nimport androidx.compose.ui.Modifier\n\n@Composable\nfun TournamentNavHost() {\n    var currentScreen by remember { mutableStateOf(\"tournaments\") }\n    Scaffold(\n        topBar = { CenterAlignedTopAppBar(title = { Text(\"Apex Esports Arena\") }) }\n    ) {\n        Box(modifier = Modifier.padding(it)) {\n            when (currentScreen) {\n                \"login\" -> LoginScreen(onSuccess = { currentScreen = \"tournaments\" })\n                \"admin\" -> AdminPanelScreen(onBack = { currentScreen = \"tournaments\" })\n                else -> TournamentListScreen(onOpenAdmin = { currentScreen = \"admin\" })\n            }\n        }\n    }\n}",
  "ui/TournamentListScreen.kt": "package com.maxy.tournament.ui\n\nimport androidx.compose.foundation.lazy.LazyColumn\nimport androidx.compose.foundation.lazy.items\nimport androidx.compose.material3.*\nimport androidx.compose.runtime.*\nimport androidx.compose.ui.Modifier\nimport androidx.compose.ui.unit.dp\nimport androidx.compose.foundation.layout.*\n\ndata class Tournament(val id: String, val title: String, val prize: String, val slots: String)\n\n@Composable\nfun TournamentListScreen(onOpenAdmin: () -> Unit) {\n    val items = listOf(\n        Tournament(\"1\", \"BGMI Pro League Season 4\", \"₹2,50,000\", \"12/20 Teams\"),\n        Tournament(\"2\", \"Free Fire Clash Squad Invitational\", \"₹1,00,000\", \"8/16 Teams\"),\n        Tournament(\"3\", \"Valorant Blitz Masters\", \"₹1,80,000\", \"4/8 Teams\")\n    )\n    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {\n        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {\n            Text(\"Active Brackets\", style = MaterialTheme.typography.titleLarge)\n            Button(onClick = onOpenAdmin) { Text(\"Admin Panel\") }\n        }\n        Spacer(Modifier.height(12.dp))\n        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {\n            items(items) { t ->\n                Card(modifier = Modifier.fillMaxWidth()) {\n                    Column(modifier = Modifier.padding(16.dp)) {\n                        Text(t.title, style = MaterialTheme.typography.titleMedium)\n                        Text(\"Prize: \${'$'}{t.prize}\", color = MaterialTheme.colorScheme.primary)\n                        Text(\"Slots: \${'$'}{t.slots}\")\n                    }\n                }\n            }\n        }\n    }\n}",
  "ui/AdminPanelScreen.kt": "package com.maxy.tournament.ui\n\nimport androidx.compose.material3.*\nimport androidx.compose.runtime.*\nimport androidx.compose.ui.Modifier\nimport androidx.compose.foundation.layout.*\nimport androidx.compose.ui.unit.dp\n\n@Composable\nfun AdminPanelScreen(onBack: () -> Unit) {\n    var title by remember { mutableStateOf(\"\") }\n    var prize by remember { mutableStateOf(\"\") }\n    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {\n        Button(onClick = onBack) { Text(\"Back to Tournaments\") }\n        Spacer(Modifier.height(16.dp))\n        Text(\"Create New Tournament Bracket\", style = MaterialTheme.typography.headlineSmall)\n        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(\"Tournament Title\") })\n        Spacer(Modifier.height(8.dp))\n        OutlinedTextField(value = prize, onValueChange = { prize = it }, label = { Text(\"Prize Pool Amount\") })\n        Spacer(Modifier.height(16.dp))\n        Button(onClick = { /* Save to Room/Firebase */ }) { Text(\"Publish Tournament\") }\n    }\n}"
}
    """.trimIndent()
}
