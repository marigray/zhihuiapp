<template>
    <div>
        <nav-bar :title="navTitle" show-back="true">
        </nav-bar>
        <list class="list" v-if="comments && comments.length!=0">
            <cell class='comment-cell' v-for="(item,index) in comments" :key="index">
                <image :src="item.headimgurl" class='header'></image>
                <div class='comment-right-content'>
                    <div :style="{flexDirection: 'row'}">
                        <text class='comment-nick-name'>{{item.uname}}</text>
                        <text class='comment-date'>{{formatDate(item.createDate/1000)}}</text>
                    </div>
                    <text class='comment-content'>{{item.content}}</text>
                </div>
                <div class='comment-line'></div>
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
      courseId: 37,
      pid: 0,
      comments: null,
      navTitle: '评论详情'
    }
  },
  created () {
    this.getComments()
  },
  methods: {
    getComments () {
      let self = this
      api.get('/edu/comment/getCommenPageByCourseNew', {cid: this.courseId, pid: this.pid},
        function (data) {
          self.comments = data.content.records
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
      api.post('/edu/comment/addComment', {cid: this.courseId, uid: user.userId(), content: text, pid: this.pid},
        function (data) {
          weex.requireModule('modal').toast({
            message: '评论成功',
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

    .no-data {
        flex: 1;
    }

    .list {
        flex: 1;
    }

    .comment-cell {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .header {
        margin-left: 30px;
        margin-top: 40px;
        width: 80px;
        height: 80px;
        border-radius: 40px;
    }

    .comment-right-content {
        display: flex;
        margin-top: 60px;
        margin-left: 20px;
        flex-direction: column;
        flex: 1;
    }

    .comment-nick-name {
        color: #646464;
        font-size: 24px;
    }

    .comment-content {
        margin-top: 20px;
        font-size: 24px;
        color: #333333;
    }

    .comment-line {
        margin-top: 30px;
        height: 2px;
        margin-left: 30px;
        width: 720px;
        background-color: #dcdcdc;
    }

    .comment-date {
        color: #646464;
        font-size: 24px;
        margin-right: 20px;
    }
</style>
