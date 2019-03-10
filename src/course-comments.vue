<template>
    <div>
        <nav-bar :title="navTitle" show-back="true">
            <image slot="right-icon1" class="comment-btn" :src="iconPath('edit_white')" @click="onComment"></image>
        </nav-bar>
        <list class="list" v-if="comments && comments.length!=0">
            <cell class='comment-cell' v-for="(item,index) in comments" :key="index" @click="onDetail(item)">
                <image :src="item.comment.headimgurl" class='header'></image>
                <div class='comment-right-content'>
                    <div :style="{flexDirection: 'row'}">
                        <text class='comment-nick-name'>{{item.comment.uname}}</text>
                        <text class='comment-date'>{{formatDate(item.comment.createDate/1000)}}</text>
                    </div>
                    <text class='comment-content'>{{item.comment.content}}</text>
                    <div :style="{flexDirection: 'row', alignItems: 'center'}">
                        <div class="gap"></div>
                        <image class='comment-count-icon' :src="iconPath('comment-icon')"></image>
                        <text class='comment-count'>{{item.count}}</text>
                    </div>
                </div>
                <div class='comment-line'></div>
            </cell>
        </list>
        <div class="no-data" v-if="comments && comments.length==0">
            <page-load-error type="no-data"></page-load-error>
        </div>
        <comment-dialog @commentSubmit="commentSubmit" title="评论" @commentCancel="commentCancel"
                        v-if="showCommentDialog"></comment-dialog>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import user from '@/modules/user'
import PageLoadError from './components/page-load-error'
import mixin from '@/modules/mixin'
import CommentDialog from './components/CommentDialog'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar, PageLoadError, CommentDialog},
  data () {
    return {
      courseId: 37,
      comments: null,
      navTitle: '评论详情',
      showCommentDialog: false
    }
  },
  created () {
    this.getComments()
  },
  methods: {
    onComment () {
      this.showCommentDialog = true
    },
    commentCancel () {
      this.showCommentDialog = false
    },
    commentSubmit (e) {
      let self = this
      api.post('/edu/comment/addComment', {cid: this.courseId, uid: user.userId(), content: e},
        function (data) {
          weex.requireModule('modal').toast({
            message: '评论成功',
            duration: 1
          })
          self.showCommentDialog = false
          self.getComments()
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e.msg,
            duration: 1
          })
        })
    },
    getComments () {
      let self = this
      api.get('/edu/comment/getCommenPageByCourseNew', {cid: this.courseId},
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
    onDetail (item) {
      navigator.push({
        url: api.getUri('course-comments-detail') + '?courseId=' + this.courseId + '&pid=' + item.comment.id,
        animated: true
      })
    }
  }
}

</script>

<style scoped>
    .comment-btn {
        width: 50px;
        height: 50px;
        position: absolute;
        right: 20px;
        top: 25px;
    }

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

    .gap {
        flex: 1;
    }

    .comment-count-icon {
        width: 53px;
        height: 44px;
    }

    .comment-count {
        height: 20px;
        margin-right: 40px;
        line-height: 20px;
        color: darkgray;
    }
</style>
