[![Build Status](https://travis-ci.org/guilgaly/cowsay-online.svg?branch=master)](https://travis-ci.org/guilgaly/cowsay-online)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2a19168f93ec47f89e5236141477e5d5)](https://www.codacy.com/app/guilgaly/cowsay-online?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=guilgaly/cowsay-online&amp;utm_campaign=Badge_Grade)

# cowsay-online

An implementation of cowsay as a webapp, because we all know that of course
everything is better in the Cloud :cloud:. Written in Scala with Akka HTTP.

It aims to provide, in a single app:

- a webpage to manually generate cowsay outputs
- a RESTful API providing the same service
- Slack integration

Props to Tony Monroe for the original cowsay program.

```text
 _____________________________
< Cows ♥ Scala and Akka HTTP! >
 -----------------------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||
```

See also:

- [Wikipedia](https://en.wikipedia.org/wiki/Cowsay) for more info on cowsay
- [tnalpgge/rank-amateur-cowsay](https://github.com/tnalpgge/rank-amateur-cowsay)
for the original program
- [guilgaly/cowsay4s](https://github.com/guilgaly/cowsay4s) for the
re-implementation of cowsay as a Scala library, used in this project
