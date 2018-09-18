Single Page Applications (SPAs) in ClojureScript, while often simple, are not always easy to understand. This example project demonstrates what I think is the best way to build SPAs today.

## Usage

Make sure you have [yarn](https://yarnpkg.com/en/docs/install) and the [Clojure CLI tools](https://clojure.org/guides/getting_started) installed.

At development time, run

```
scripts/dev

# or to clean all build artifacts

scripts/dev --reset
```

to start a reloading figwheel server, then open http://localhost:9333/ in your browser.

To build a production build, run

```
scripts/prod
```

and open `dist/index.html` in the browser.

The script applies advanced optimizations by default. You can request a non-minified build using `scripts/prod --simple`.


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

- Error management

  Use Promise rejections to signal errors.

## Technical details

### Reagent

[Reagent](https://cljdoc.xyz/d/reagent/reagent/0.8.1/doc/documentation-index) is deservedly the most popular React wrapper. With its syntax based on hiccup — the s-expression syntax that in a dream world HTML would have used from the start — and the Ratom reloading model, it is simple enough for beginners and flexible enough for experts.

### Figwheel Main

Use [Figwheel Main](https://figwheel.org/) as the build tool. It's faster, cleaner and actively developed.

### Webpack doublebundle

To require NPM dependencies (including, but not limited to, React components) with great reliability, use Webpack to create an auxiliary bundle and include it in the main build via `foreign-libs`. For more on the rationale, see [this post](https://github.com/pesterhazy/presumably/blob/master/posts/double-bundle.md) and the [official guide](https://clojurescript.org/guides/webpack).

### Router5

The [Router5](https://github.com/router5/router5) library offers a data-centric and framework-agnostic routing system. In addition to being built on clean abstractions, it supports registering on-activate and on-deactivate hooks to trigger side-effects. A common use case is to load data when you enter a page, or to clean up resources when you leave a page. See [Past and future of client-side routing](https://www.youtube.com/watch?v=hblXdstrAg0) by Router5's author, Thomas Roch

## Code conventions

The `-ui` suffix for function is used to indicate that the function is a Reagent component and should be used in `[square-brackets]`.

Every subpage of the app lives in a separate namesapce in the `cljs-spa.page` hierarchy. It exposes a `page-ui` entry point, as well as optional `on-activate` and `on-deactivate` hooks.

A page is in one of three states: `:loading`, `:loaded` or `:failed`. The page-state-ui wrapper shows a spinner while loading, and a sad smiley when the on-activate promise failed.

The app relies on higher-order components like [layout-ui](https://github.com/pesterhazy/cljs-spa-example/blob/master/src/cljs_spa/layout.cljs#L36) and [router-ui](https://github.com/pesterhazy/cljs-spa-example/blob/master/src/cljs_spa/router.cljs#L47) to hide complexity and for better composability.

## Testing

Tests use the excellent Extra Main facility. With `scripts/dev` running, visit http://localhost:9333/figwheel-extra-main/test to see the [cljs-test-display](https://github.com/bhauman/cljs-test-display) test runner.

The tests in this project are expected to fail - it's easier to see that tests are working when they don't pass.

Additionally, this project is set up for automatic testing using a headless browser:

```
scripts/tests
```

## Highlights

Given that we use webpack, any React component can be used easily. As an example, the Home page includes the excellent [react-select](https://github.com/JedWatson/react-select) component. See [code](src/cljs_spa/page/home.cljs).

## Troubleshooting

When you run into surprising behavior in the browser, the first thing to try is to restart the dev env using

```
scripts/dev --reset
```

which clears local compilation caches.

## Links

This repository is inspired by Richard Feldman's [elm-spa-example](https://github.com/rtfeldman/elm-spa-example/).

Don't forget to check out [Figwheel Main](https://figwheel.org/).

## License

MIT

## Author

Paulus Esterhazy <pesterhazy@gmail.com>
