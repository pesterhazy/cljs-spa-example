Single Page Applications (SPAs) in ClojureScript, while often simple, are not always easy to understand. This example project demonstrates what I think is the best way to build SPAs today.

## Usage

Make sure you have [yarn](https://yarnpkg.com/en/docs/install) and the [Clojure CLI tools](https://clojure.org/guides/getting_started) installed.

Then run

```
scripts/dev
```

Then open http://localhost:9333/ in your browser

## Principles

- Data-driven routing

  Navigation is a concern of the M and C of your MVC application, not of the V, so routing should be decoupled from the view layer of your application.

- Single global state

  A single global state atom is a simple and clear way to manage state. It makes it obvious where state resides. This project uses a single ratom, but to keep things simple it doesn't introduce a state management a la re-frame.

- Explicit resource management

  When the user enters a page, the app needs to perform asynchronous side-effects (often network requests) and acquire resources (set up event listeners, timers, stateful objects). Conversely, resources need to be disposed of when leaving the page. Page resource management should be explicitly tied to navigation events, rather than component lifecycle methods.

- Reloadability

  Hot reloading while keeping state is critical for developer productivity.

- Embrace NPM

  ClojureScript rocks, but JavaScript reaches - and it has a powerful ecosystem. Because CLJS has great JS interop, prefer state-of-the-art NPM libraries over less powerful ClojureScript alternatives. Instead of CLJS-specific CLJSJS jars, directly tap NPM, the delivery path used by thousands of developers.

- Embrace the Web Platform

  Modern JS engines ship with high-quality abstractions like fetch and ES6 promises. Use these over CLJS alternatives.

## Technical details

- Figwheel Main

  Use Figwheel Main as the build tool. It's faster, cleaner and actively developed.

- Webpack doublebundle

  To require NPM dependencies (including, but not limited to, React components) with great reliability, use Webpack to create an auxiliary bundle and include it in the main build via `foreign-libs`.

- Use router5

  The [router5](https://github.com/router5/router5) is a data-centric and framework-agnostic router. It supports registering on-activate and on-deactivate hooks to trigger side-effects. A common use case is to load data when you enter a page, or to clean up resources when you leave a page.

## License

MIT

## Author

Paulus Esterhazy <pesterhazy@gmail.com>
