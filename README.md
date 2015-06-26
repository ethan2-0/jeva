Jeva
====

What is Jeva?
-------------
Jeva is a minimalist Java web framework that keeps REST in mind. It's intended to feel familiar if you already work
with Flask. What I didn't like about all of the existing Java web frameworks is the amount of complexity. By contrast,
Jeva is designed to just be *simple*; to be natural. What I mean by natural is natural in the way Flask is natural:

    from flask import Flask
    app = Flask(__name__)
    @app.route("/")
    def serveRoot():
      return "Hello world!"
    app.run()

The equivalent Jeva code is:

    import jeva.*;
    public class Test {
      public static void main(String[] args) throws IOException {
        Jeva jeva = new Jeva(new Test());
        jeva.run(8000);
      }
      @Serves(path="/")
      public String serveRoot() {
        return "Hello world!";
      }
    }

In the same way that Flask just *works*, Jeva does the same.

Why not just Flask?
-------------------
For many applications, Flask is ideal. But, sometimes, you don't want Python; you want Java. That's where Jeva comes
in. That's why I want Jeva to feel familiar; my goal was to lower the barrier to entry.

Documentation and Examples
==========================
Javadoc is available in /javadoc or [here](https://ethan2-0.github.io/jeva_javadoc/).

The example is in the package jeva.example.

Getting the source
==================
Clone the repo in Eclipse.

Distribution, embedding, and prepackaged JARs
=============================================
I would not recommend including Jeva seperately on the classpath; rather, I intend you to keep the Jeva source in the
same JAR file as your actual application (w/o the example and possibly the tests, depending on how much modifiying
of Jeva you'll be doing). Despite that, it is obviously possible to keep Jeva seperately on the classpath from your
application. That way, the JAR is standalone. Again, there's nothing stopping you from building a seperate Jeva JAR.
