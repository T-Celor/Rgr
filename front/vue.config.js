const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8080,
    proxy: {
      "/yahoo": {
        target: "https://query1.finance.yahoo.com",
        changeOrigin: true,
        pathRewrite: {
          "^/yahoo": "",
        },
      },
      "http://rgrback-route": {
        target: "http://localhost:8081",
        changeOrigin: true,
        pathRewrite: {
          "^/http://rgrback-route": "",
        },
      },
    },
    client: {
      overlay: {
        runtimeErrors: (error) => {
          const ignoreErrors = [
            "ResizeObserver loop limit exceeded",
            "ResizeObserver loop completed with undelivered notifications.",
          ];
          if (ignoreErrors.includes(error.message)) {
            return false;
          }
          return true;
        },
      },
    },
  },
});
