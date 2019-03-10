<template>
    <div>
        <nav-bar show-back="true" title="详情">
            <image slot="right-icon2" class="share-btn" :src="iconPath('edit_white')" @click="jumpEvaluate"></image>
        </nav-bar>
        <list v-if="detailInfo">
            <cell class="cuorse-image">
                <image :src="detailInfo.image" class="cuorse-image"></image>
                <div class="play-btn-layer" v-if="detailInfo.type===1">
                    <image class="play-btn" :src="iconPath('banner_video')" @click="getVideoURL(detailInfo.video)"></image>
                </div>
            </cell>
            <cell class="detail-info">
                <text class="teacher-name">{{teacherInfo.name}}</text>
                <text class="message-btn" @click="onMessage">私信老师</text>
            </cell>
            <cell>
                <div class="detail-head-content">
                    <div>
                        <text class="head-content">{{detailInfo.name}}</text>
                    </div>
                    <text class="type">{{CourseTypeString(detailInfo.type)}}</text>
                    <text class="course-time">上课时间：{{formatDate(detailInfo.startDate/1000)}}</text>
                </div>
                <cell class='line-gap'></cell>
            </cell>
            <cell v-if="detailInfo.type===2" class="file-list">
                <div class="file-head">
                    <text class="list-icon"></text>
                    <text class="list-title">课程地址</text>
                </div>
                <text class="address">{{detailInfo.address}}</text>
            </cell>
            <cell class="file-list" v-if="detailInfo.type===3">
                <div class="file-head">
                    <text class="list-icon"></text>
                    <text class="list-title">文件目录</text>
                    <text class="list-right-title">全部{{chaptersLength}}个章节</text>
                </div>
                <div class="file-list-content" v-for="(item,index) in chapters" :key="index" @click="getVideoURL(item.url)">
                    <text class="file-list-title">{{item.chaptername}}</text>
                    <text class="file-list-btn">观看</text>
                </div>
            </cell>
            <cell class='line-gap'></cell>
            <cell class="file-list">
                <div class="file-head">
                    <text class="list-icon"></text>
                    <text class="list-title">课程问卷</text>
                    <text class="list-right-title">{{surveysLength}}</text>
                </div>
                <div class="file-list-content" v-for="(item,index) in surveys" :key="index" @click="gotoTosurveys(item)">
                    <text class="file-list-title">{{item.name}}</text>
                    <text class="file-list-btn"
                          :class="[(item.status===1)?'active':'']">{{item.status===0?'去完成':'已完成'}}
                    </text>
                </div>
            </cell>
            <cell class='line-gap'></cell>
            <cell class="file-list">
                <div class="file-head">
                    <text class="list-icon"></text>
                    <text class="list-title">课程通知</text>
                    <text class="list-right-title">{{messagesLength}}</text>
                </div>
                <div class="file-list-content" v-for="(item,index) in messages" :key="index">
                    <text class="file-list-title">{{item.content}}</text>
                    <!--<text class="file-list-btn"-->
                          <!--:class="[(item.status===1)?'active':'']">{{item.status===0?'未读':'已读'}}-->
                    <!--</text>-->
                </div>
            </cell>
            <cell class="qrcode-content">
                <image class="qrcode-image" :src="detailInfo.qrcode" @click="preview"></image>
            </cell>
        </list>
        <comment-dialog @commentSubmit="commentSubmit" title="私信" @commentCancel="commentCancel"
                        v-if="showCommentDialog"></comment-dialog>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import user from '@/modules/user'
import mixin from '@/modules/mixin'
import CommentDialog from './components/CommentDialog'
import Nat from 'natjs'

var navigator = weex.requireModule('navigator')
var globalEvent = weex.requireModule('globalEvent');

export default {
  mixins: [mixin],
  components: {NavBar, CommentDialog},
  data () {
    return {
      courseId: 37,
      detailInfo: null,
      teacherInfo: null,
      showCommentDialog: false,
      showShare: false,
      videoURL: null,
      readToPlay: false,
      hidePay: false,
      isEnroll: false,
      loadingStatus: 'loading',
      enrollNum: 0,
      collectionNum: 0,
      type: 1,
      chapters: [],
      surveys: [],
      messages: []
    }
  },
  created () {
    this.getDetail()
    globalEvent.addEventListener('survey-answered', function (e) {
      let result = e.result
      let id = result.id
      for (let i = 0; i < this.surveys.length; i++) {
        let item = this.surveys[i]
        if (item.surveyId === id) {
          item.status = 1
        }
      }
    }.bind(this))
  },
  computed: {
    chaptersLength () {
      return this.chapters.length
    },
    messagesLength () {
      return this.messages.length
    },
    surveysLength () {
      return this.surveys.length
    }
  },
  methods: {
    gotoTosurveys (item) {
      if (item.status === 1) return
      navigator.push({
        url: api.getUri('questionnaire-answer') + '?id=' + item.surveyId,
        animated: true
      })
    },
    jumpEvaluate () { // 课程评价
      navigator.push({
        url: api.getUri('course-comments' + '?courseId=' + this.courseId + '&navTitle=' + this.detailInfo.name),
        animated: true
      })
    },
    getDetail () {
      let self = this
      api.get('/edu/course/getCourseInfoById', {id: this.courseId, uid: user.userId()},
        function (data) {
          self.detailInfo = data.content.course
          self.isEnroll = data.content.isEnroll
          self.enrollNum = data.content.enrollNum
          self.collectionNum = data.content.collectionNum
          self.chapters = data.content.chapter.data
          self.teacherInfo = data.content.userInfo
          self.surveys = data.content.survey.data
          self.messages = data.content.message.data
        },
        function () {

        })
    },
    getVideoURL (videoPath) {
      if (!this.isEnroll) {
        this.pay()
        return
      }

      let self = this
      api.get('/edu/video/getRealDownloadUrl', {downloadUrl: videoPath},
        function (data) {
          let videoURL = data.content
          weex.requireModule('file').playVideo({url: videoURL,playall:true})
        },
        function () {

        })
    },

    onMessage () {
      this.showCommentDialog = true
    },
    commentCancel () {
      this.showCommentDialog = false
    },
    preview () {
      Nat.image.preview([
        this.detailInfo.qrcode
      ])
    },
    commentSubmit (e) {
      let self = this
      api.post('/edu/message/addMessage',
        {
          sendToIds: this.teacherInfo.id,
          sendFrom: user.userId(),
          content: e,
          type: 3
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '私信发送成功',
            duration: 1
          })
          self.showCommentDialog = false
        },
        function () {
          weex.requireModule('modal').toast({
            message: '私信发送失败，请稍后再试',
            duration: 1
          })
        })
    }
  }
}
</script>

<style scoped>
    .share-btn {
        width: 50px;
        height: 50px;
        position: absolute;
        right: 50px;
        top: 25px;
    }

    .cuorse-image {
        width: 750px;
        height: 400px;
        background-color: rgba(0, 0, 0, 0.5);
    }

    .detail-info {
        width: 750px;
        height: 70px;
        flex-direction: row;
        align-items: center;
        padding-left: 20px;
        padding-right: 20px;
        border-bottom-width: 1px;
        border-color: #c0c0c0;
        border-style: solid;
    }

    .teacher-name {
        font-size: 24px;
        color: #333333;
    }

    .message-btn {
        position: absolute;
        border-radius: 5px;
        background-color: rgb(15, 54, 145);
        color: #fff;
        right: 20px;
        font-size: 24px;
        padding: 5px 10px;
        margin-top: 20px;
    }

    .detail-head-content {
        width: 710px;
        padding: 20px;
    }

    .head-content {
        font-size: 24px;
        color: #333;
        width: 710px;
    }

    .type {
        margin-top: 10px;
        background-color: rgb(15, 54, 145);
        color: #fff;
        border-radius: 5px;
        padding: 5px 10px;
        font-size: 22px;
        height: 40px;
        width: 70px;
    }

    .line-gap {
        width: 750px;
        height: 20px;
        background-color: #f0f0f0;
    }

    .file-list {
        width: 750px;
    }

    .list-icon {
        width: 10px;
        height: 40px;
        margin-left: 20px;
        background-color: rgb(15, 54, 145);
    }

    .file-list-content {
        display: flex;
        flex-direction: row;
        margin-top: 10px;
        height: 70px;
        width: 710px;
        margin-left: 20px;
        align-items: center;
    }

    .list-title {
        margin-left: 10px;
        height: 40px;
        color: rgb(15, 54, 145);
        line-height: 40px;
    }

    .list-right-title {
        position: absolute;
        right: 20px;
        font-size: 24px;
        top: 20px;
        color: rgb(15, 54, 145);

    }

    .file-head {
        height: 80px;
        border-bottom-width: 1px;
        border-color: #c0c0c0;
        border-style: solid;
        align-items: center;
        flex-direction: row;
    }

    .file-list-title {
        font-size: 24px;
        color: #333333;
        left: 20px;
        height: 60px;
        line-height: 60px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex: 1;
        margin-right: 10px;
    }

    .file-list-btn {
        margin-right: 10px;
        font-size: 22px;
        background-color: rgb(15, 54, 145);
        color: #fff;
        border-radius: 5px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        padding-left: 10px;
        padding-right: 10px;
    }

    .active {
        background-color: #C6C6C6
    }

    .qrcode-content {
        width: 750px;
        height: 200px;
        background-color: #f0f0f0;
    }

    .qrcode-image {
        width: 150px;
        height: 150px;
        top: 25px;
        left: 300px;
    }

    .detail-head-address {
        left: 20px;
        width: 710px;
        max-height: 70px;
        overflow: hidden;
        bottom: 10px;
    }

    .play-btn-layer {
        left: 0px;
        top: 0px;
        bottom: 0px;
        right: 0px;
        justify-content: center;
        align-items: center;
        background-color: rgba(0, 0, 0, 0.5);
        position: absolute;
    }

    .play-btn {
        width: 100px;
        height: 100px;
    }

    .address {
        font-size: 24px;
        color: #333;
        margin-left: 40px;
        width: 670px;
        margin-top: 20px;
        margin-bottom: 20px;
    }

    .course-time{
        margin-top: 20px;
        font-size: 28px;
        color: #969696;
    }

</style>
