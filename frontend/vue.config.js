module.exports = {
    // Configure webpack to play nicely with the backend
    devServer: {
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    // Disable eslint errors during development
    lintOnSave: false
  }