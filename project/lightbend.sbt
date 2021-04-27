resolvers in ThisBuild += "lightbend-commercial-mvn" at
        "https://repo.lightbend.com/pass/PK7Y5qLcw-7CTfMqEn7YjLesRQ4-rCJRbMTUgDDnXYbyfgjo/commercial-releases"
resolvers in ThisBuild += Resolver.url("lightbend-commercial-ivy",
        url("https://repo.lightbend.com/pass/PK7Y5qLcw-7CTfMqEn7YjLesRQ4-rCJRbMTUgDDnXYbyfgjo/commercial-releases"))(Resolver.ivyStylePatterns)

