<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>
<script>
  export default {
    name: 'App',
    watch: {
      $route(to,from) {
        console.log("to")
        console.log(to)
        if(to.path != '/login') {
          let obj = {
            name: to.name,
            title: to.meta.title
          }
          this.$store.commit("addTabs",obj)
        }
      }
    }
  }
</script>
<style>
html,body,#app{
  height: 100%;
  margin: 0;
  border: 0;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

</style>
