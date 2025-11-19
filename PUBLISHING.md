# Publishing and Consuming the Axon JPA Connector

## Package Information

- **Group ID**: `wtf.milehimikey`
- **Artifact ID**: `axon-jpa-connector`
- **Version**: `1.0.0`
- **Repository**: GitHub Packages (Maven)
- **URL**: `https://maven.pkg.github.com/milehimikey/axon-jpa-connector`

## For Consumers: How to Use This Package

### Step 1: Create a GitHub Personal Access Token

1. Go to [GitHub Settings > Developer settings > Personal access tokens > Tokens (classic)](https://github.com/settings/tokens)
2. Click "Generate new token (classic)"
3. Give it a name like "Maven Packages Read"
4. Select the `read:packages` scope
5. Click "Generate token"
6. **Copy the token immediately** (you won't see it again)

### Step 2: Configure Gradle Credentials

Add your GitHub credentials to `~/.gradle/gradle.properties`:

```properties
gpr.user=your-github-username
gpr.token=ghp_your_personal_access_token_here
```

**Note**: This file should NOT be committed to version control.

### Step 3: Add Repository and Dependency to Your Project

In your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/milehimikey/axon-jpa-connector")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("wtf.milehimikey:axon-jpa-connector:1.0.0")
    
    // Add your database driver
    runtimeOnly("org.postgresql:postgresql")  // or mysql, h2, etc.
}
```

### Step 4: Verify the Dependency

Run:
```bash
./gradlew dependencies --configuration runtimeClasspath | grep axon-jpa-connector
```

You should see:
```
\--- wtf.milehimikey:axon-jpa-connector:1.0.0
```

## For Publishers: How to Publish This Package

### Prerequisites

1. GitHub Personal Access Token with `write:packages` and `read:packages` scopes
2. Credentials configured in `~/.gradle/gradle.properties`:

```properties
gpr.user=milehimikey
gpr.token=ghp_your_personal_access_token_here
```

### Publishing Commands

**Publish to GitHub Packages:**
```bash
./gradlew publish
```

**Or specifically to GitHub Packages:**
```bash
./gradlew publishAllPublicationsToGitHubPackagesRepository
```

**Publish to local Maven repository (for testing):**
```bash
./gradlew publishToMavenLocal
```

### Verify Publication

After publishing, the package will be available at:
- **Web UI**: https://github.com/milehimikey/axon-jpa-connector/packages
- **Maven URL**: https://maven.pkg.github.com/milehimikey/axon-jpa-connector/wtf/milehimikey/axon-jpa-connector/1.0.0/

### Making the Package Public

1. Go to https://github.com/milehimikey/axon-jpa-connector/packages
2. Click on the package name
3. Click "Package settings" (right sidebar)
4. Scroll to "Danger Zone"
5. Click "Change visibility"
6. Select "Public"
7. Confirm

**Note**: Even public packages on GitHub require authentication to download.

## CI/CD Publishing (GitHub Actions)

Create `.github/workflows/publish.yml`:

```yaml
name: Publish Package

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Publish to GitHub Packages
        run: ./gradlew publish
        env:
          GITHUB_ACTOR: ${{ github.actor }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## Troubleshooting

### "Could not find wtf.milehimikey:axon-jpa-connector:1.0.0"

- Verify the package has been published to GitHub Packages
- Check your credentials in `~/.gradle/gradle.properties`
- Ensure your GitHub token has `read:packages` scope

### "401 Unauthorized"

- Your GitHub token is invalid or expired
- Regenerate your token with the correct scopes

### "403 Forbidden"

- Your token doesn't have the required scopes
- Add `read:packages` scope to your token

## Alternative: Using Environment Variables

Instead of `gradle.properties`, you can use environment variables:

```bash
export GITHUB_ACTOR=your-github-username
export GITHUB_TOKEN=ghp_your_token_here
./gradlew build
```

