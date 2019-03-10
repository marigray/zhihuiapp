<template>
    <div>
        <nav-bar :title="navTitle" show-back="true">
        </nav-bar>
        <list class="list" v-if="comments && comments.length!=0">
            <cell class='comment-cell' v-for="(item,index) in comments" :key="index">
                <div class="personal-cell-content" v-if="item.sendFromId != currentUserId">
                    <image :src='item.sendFromHeadimag' class='header'></image>
                    <div class='personal-cell-right-content'>
                        <div class="cell-name-row">
                            <text class='personal-nick-name'>{{item.sendFromName}}</text>
                        </div>
                        <text class='personal-content'>{{item.content}}</text>
                        <text class='system-date'>{{formatDate(item.sendDate/1000)}}</text>
                    </div>
                </div>
                <div :class="['personal-cell-content', 'personal-cell-content-right']" v-else>
                    <image :src='item.sendFromHeadimag' class='header'></image>
                    <div class='personal-cell-right-content'>
                        <div class="cell-name-row">
                            <text class='personal-nick-name'>{{item.sendFromName}}</text>
                        </div>
                        <text class='personal-content'>{{item.content}}</text>
                        <text class='system-date'>{{formatDate(item.sendDate/1000)}}</text>
                    </div>
                </div>
            </cell>
        </list>
        <div class="no-data" v-if="comments && comments.length==0">
            <page-load-error type="no-data"></page-load-error>
        </div>
        <comment-input @send="send"></comment-input>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import user from '@/modules/user'
import PageLoadError from './components/page-load-error'
import CommentInput from './comment-input'
import mixin from '@/modules/mixin'

export default {
  mixins: [mixin],
  components: {NavBar, PageLoadError, CommentInput},
  data () {
    return {
      uidother: 0,
      comments: null,
      navTitle: '私信详情'
    }
  },
  created () {
    this.getComments()
  },
  computed: {
    currentUserId () {
      return user.userId()
    }
  },
  methods: {
    getComments () {
      let self = this
      api.get('/edu/message/getPrivateMessageDetail', {uid: user.userId(), uidother: this.uidother},
        function (data) {
          self.comments = data.content
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e,
            duration: 1000
          })
        })
    },
    send (text) {
      let self = this
      api.post('/edu/message/addMessage', {sendToIds: this.uidother, sendFrom: user.userId(), content: text, type: 3},
        function (data) {
          weex.requireModule('modal').toast({
            message: '发送成功',
            duration: 1
          })
          self.getComments()
        },
        function () {

        })
    }
  }
}

</script>

<style scoped>
    .system-date {
        width: 200px;
        margin-right: 10px;
        margin-bottom: 10px;
        text-align: center;
        font-size: 24px;
        color: #969696;
    }

    .no-data {
        flex: 1;
    }

    .list {
        flex: 1;
        background-color: rgb(240, 240, 240);
    }

    .comment-cell {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .header {
        margin-left: 30px;
        margin-top: 20px;
        width: 80px;
        height: 80px;
        border-radius: 40px;
    }

    .personal-cell {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .cell-name-row {
        flex-direction: row;
    }

    .personal-cell-content {
        align-items: center;
        flex-wrap: wrap;
        flex-direction: row;
        margin: 20px;
        width: 500px;
        border: 1px solid #eee;
        border-radius: 10px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        background-color: white;
    }

    .personal-cell-content-right {
        margin-left: 220px;
    }

    .header {
        margin-left: 30px;
        margin-top: 20px;
        margin-bottom: 35px;
        width: 80px;
        height: 80px;
        border-radius: 40px;
    }

    .personal-cell-right-content {
        display: flex;
        margin-left: 20px;
        flex-direction: column;
        justify-content: center;
        flex: 1;
    }

    .personal-nick-name {
        flex: 1;
        margin-top: 30px;
        color: #000000;
        font-size: 24px;
    }

    .personal-content {
        margin-top: 10px;
        margin-right: 10px;
        margin-bottom: 20px;
        font-size: 24px;
        color: #969696;
    }

</style>
