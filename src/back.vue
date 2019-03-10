<template>
    <div>
        <nav-bar title="Title"></nav-bar>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'

export default {
  components: {NavBar},
  data () {
    return {}
  }
}
</script>

<style scoped>
    .btn {
        height: 100px;
        display: flex;
        align-items: center;
    }

    .line {
        height: 1px;
        width: 740px;
        margin-left: 10px;
        background-color: lightgray;
    }

    .btn-text {
        display: flex;
        flex: 1;
        margin-left: 20px;
        font-size: 24px;
    }

    .arrow {
        margin-right: 20px;
        width: 20px;
        height: 20px;
    }
</style>
