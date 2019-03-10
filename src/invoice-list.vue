<template>
    <div>
        <nav-bar show-back="true" title="发票列表"></nav-bar>
        <list class="list" v-if="lists && lists.length!=0">
            <cell class='list-cell' v-for="(item,index) in lists" :key="index">
                <text class="name">{{item.title}}</text>
                <image class="icon" :src="iconPath('icon_right')"></image>
                <div class='list-line'></div>
            </cell>
        </list>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import mixin from '@/modules/mixin'
import user from '@/modules/user'

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      lists: []
    }
  },
  created () {
    this.getLists()
  },
  methods: {
    getLists () {
      let self = this
      api.get('/edu/invoice/getInvoicePage', {userid: user.userId()},
        function (data) {
          self.lists = data.content.records
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e,
            duration: 1000
          })
        })
    }
  }
}
</script>

<style scoped>
    .list {
        flex: 1;
    }

    .name {
        margin-left: 20px;
        font-size: 30px;
    }

    .list-cell {
        display: flex;
        flex-direction: row;
        width: 720px;
        height: 100px;
        align-items: center;
    }

    .list-line {
        position: absolute;
        height: 2px;
        bottom: 0px;
        width: 700px;
        left: 50px;
        background-color: #dcdcdc;
    }

    .icon {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 30px;
        right: 20px;
    }

</style>
