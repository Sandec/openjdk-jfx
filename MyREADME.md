


# Publish:
1.
`./gradlew sdk graphics:publishToMavenLocal base:publishToMavenLocal`

2. publish all local:`
`./gradlew publishToMavenLocal`

3. publish online
`./gradlew clean sdk bintrayUpload`

4. clear cache (optional) (tested for sbt)
`find ~/.m2 ~/.ivy2 | grep openjfx | grep jpro | xargs rm -r`


### jerry picks:

git cherry-pick <commit> <commit>

JDK-8217492: memory leak after the event WindowEvent.DESTROY
b881260e65c8ab8a8ce720e50b669848aa1a5889

JDK-8216377: Fix memoryleak for initial nodes of Window
7ea49ce15387e074d167134fec684b0794081fd8


# rebase:
git pull --rebase https://github.com/javafxports/openjdk-jfx.git jfx-11