<template>
    <div @viewappear="viewappear">
        <div class="bg">
            <text class='nav-title'>课程处理</text>
            <text class="nav-btn" @click="showHistory">历史课程</text>
        </div>
        <div class="content">
            <div class='cell-category-header-container'>
                <div class='cell-category-header-line'></div>
                <text class='cell-category-header-title'>进行中的课程</text>
            </div>
            <list class="list" show-scrollbar=false @loadmore="loadMore">
                <cell v-for="(item,index) in courseList" :key="index" @click="onDetail(item.courseInfo)">
                    <div class="row">
                        <div class="row-left">
                            <div class="row-left-bottom" @click="uploadQRCode(item.courseInfo)">
                                <text class="row-left-bottom-text">请上传二维码</text>
                            </div>
                            <image class="course-image" :src="item.courseInfo.image">
                            </image>
                            <div class="row-left-bottom-qr">
                                <image class="qr-image" :src=item.courseInfo.qrcode>
                                </image>
                            </div>
                        </div>
                        <div class="row-right">
                            <text class="row-right-title" lines="2">{{item.courseInfo.name}}</text>
                            <text class="row-right-title2">课程时间：{{item.courseInfo.startDate}}</text>
                            <div class="row-right-line3">
                                <div class="btn-type">
                                    <text style="font-size:22px;color:#ffffff;">{{CourseTypeString(item.courseInfo.type)}}</text>
                                </div>
                                <text class="count-text">已有{{item.enrollNum ? item.enrollNum : 0}}人报名</text>
                            </div>
                            <div class="row-right-line4">
                                <div class="blue-btn" @click="onQuestionnaire(item)">
                                    <text class="blue-text">问卷</text>
                                </div>
                                <div :class="['blue-btn', 'blue-btn-margin']" @click="onCancel(item.courseInfo)">
                                    <text class="blue-text">取消</text>
                                </div>
                                <div :class="['blue-btn','blue-btn-margin']" @click="onNoti(item.courseInfo)">
                                    <text class="blue-text">通知</text>
                                </div>
                                <div :class="['blue-btn','blue-btn-margin']" @click="jumpEvaluate(item.courseInfo)">
                                    <text class="blue-text">评价</text>
                                </div>
                            </div>
                        </div>
                    </div>
                </cell>
                <cell class="load-cell">
                    <more-footer :status="loadingStatus"></more-footer>
                </cell>
            </list>
        </div>
        <wxc-popover ref="wxc-popover" :buttons="navPopoverButtons" :position="popoverPosition"
                     :arrowPosition="popoverArrowPosition" :text-style="{}"
                     @wxcPopoverButtonClicked="popoverButtonClicked"></wxc-popover>
        <PopupMenu v-if="isPopupShow" @popupClickedWithBtnIndex="popupClickedWithBtnIndex"
                   contentInfo="您确认要取消该课程吗" iconImage="icon_gth" :statusType="true"></PopupMenu>
        <zhprogress v-if="isPopupProgress" :progressValue=uploadProgressValue></zhprogress>
        <comment-dialog @commentSubmit="notiSubmit" title="课程通知" @commentCancel="notiCancel"
                        v-if="showNoti"></comment-dialog>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import user from '@/modules/user'
import mixin from '@/modules/mixin'
import MoreFooter from './components/more-footer'
import PopupMenu from './components/popup-menu'
import {WxcPopover} from 'weex-ui'
import Nat from 'natjs'
import zhprogress from './components/zhprogress'
import InfoPop from './components/info-popup'
import CommentDialog from './components/CommentDialog'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar, MoreFooter, PopupMenu, WxcPopover, zhprogress, InfoPop, CommentDialog},
  data () {
    return {
      courseList: [],
      loadingStatus: 'loading',
      pageNo: 1,
      isPopupShow: false,
      currentItemToCancel: null,
      isPopupProgress: false,
      currentItem: null,
      showNoti: false,
      currentNotiItem: null
    }
  },
  computed: {
    popoverArrowPosition () {
      return {pos: 'top', x: -15}
    },
    popoverPosition () {
      return {x: -6, y: 40}
    },
    navPopoverButtons () {
      return [
        {text: '已发布课程', key: 'published'},
        {text: '历史课程', key: 'history'}
      ]
    }
  },
  created () {
    this.getCourseList()

    var globalEvent = weex.requireModule('globalEvent');
    globalEvent.addEventListener('questionnaire-added', function (e) {
      let result = e.result
      api.post('/edu/survey/addSurveyCourse',
        {
          surveyid: result.id,
          cid: this.currentItem.courseInfo.id
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '问卷添加成功',
            duration: 1000
          })
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e.msg,
            duration: 1000
          })
        })
    }.bind(this))
  },
  methods: {
    notiSubmit (e) {
      let self = this
      api.post('/edu/message/publishCourseMessage',
        {
          cid: this.currentNotiItem.id,
          content: e
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '通知发送成功',
            duration: 1000
          })
          self.showNoti = false
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e.msg,
            duration: 1000
          })
        })
    },
    notiCancel () {
      this.showNoti = false
    },
    onNoti (item) {
      this.currentNotiItem = item
      this.showNoti = true
    },
    onQuestionnaire (item) {
      this.currentItem = item
      navigator.push({
        url: api.getUri('questionnaire'),
        animated: true
      })
    },
    popoverButtonClicked (menu) {
      if (menu.key === 'published') {
        navigator.push({
          url: api.getUri('coursehistory') + '?status=1',
          animated: true
        })
      } else if (menu.key === 'history') {
        navigator.push({
          url: api.getUri('coursehistory' + '?status=3'),
          animated: true
        })
      }
    },
    onNavigatorPop () {
      navigator.pop()
    },
    jumpEvaluate (item) { // 课程评价
      navigator.push({
        url: api.getUri('course-comments' + '?courseId=' + item.id + '&navTitle=' + item.name),
        animated: true
      })
    },
    onCancel (item) {
      this.isPopupShow = true
      this.currentItemToCancel = item
    },
    popupClickedWithBtnIndex (index) {
      this.isPopupShow = false
      if (index === 1) {
        api.post('/edu/course/cancelCourse',
          {
            id: this.currentItemToCancel.id
          },
          function (data) {
            weex.requireModule('modal').toast({
              message: '课程取消成功',
              duration: 1000
            })
          },
          function (e) {
            weex.requireModule('modal').toast({
              message: e.msg,
              duration: 1000
            })
          })
      }
    },
    showHistory () {
      navigator.push({
        url: api.getUri('coursehistory' + '?status=3'),
        animated: true
      })
    },
    showMore () {
      this.$refs['wxc-popover'].wxcPopoverShow()
    },
    loadMore () {
      if (this.loadingStatus === 'no-more' || this.loadingStatus === 'no-data') return
      this.pageNo++
      this.getCourseList()
    },
    getCourseList () {
      let self = this
      api.get('/edu/course/getCoursePageByTeacher',
        {
          id: user.userId(),
          status: 2,
          pageNo: this.pageNo,
          pageCount: 10
        },
        function (data) {
          self.courseList = self.courseList.concat(data.content.records)
          if (self.courseList.length === 0) {
            self.loadingStatus = 'no-data'
          } else {
            if (data.content.total === self.courseList.length) {
              self.loadingStatus = 'no-more'
            }
          }
        },
        function () {
          self.loadingStatus = 'error'
        })
    },
    onDetail (item) {
      navigator.push({
        url: api.getUri('detail') + '?courseId=' + item.id + '&hidePay=true',
        animated: true
      })
    },
    uploadQRCode (item) {
      let self = this
      Nat.image.pick()
        .then((ret) => {
          let filePath = ret.paths[0]
          weex.requireModule('file').compressor(filePath, function (compressedImageFile) {
            self.uploadProgressValue = 0
            self.isPopupProgress = true
            Nat.upload(api.getBaseUrl() + '/edu/course/uploadPic',
              {
                path: compressedImageFile,
                formData: {
                  type: 2
                },
                name: 'aaaaa.png',
                mimeType: 'png'
              },
              {
                onProgress: (p) => {
                  self.uploadProgressValue = Math.floor(p * 100)
                }
              }, (err, res) => {
                self.isPopupProgress = false
                if (err) {
                  weex.requireModule('modal').toast({
                    message: JSON.stringify(err),
                    duration: 1000
                  })
                  return
                }
                let data = JSON.parse(res.data)
                if (data.httpCode === 200) {
                  let url = data.content
                  // 更新课程
                  api.post('/edu/course/updateCourse', {id: item.id, qrcode: url},
                    function (data) {
                      weex.requireModule('modal').toast({
                        message: '二维码上传成功',
                        duration: 1000
                      })
                      item.qrcode = url
                    },
                    function (e) {
                      weex.requireModule('modal').toast({
                        message: '二维码上传失败',
                        duration: 1000
                      })
                    })
                } else {
                  weex.requireModule('modal').toast({
                    message: '二维码上传失败',
                    duration: 1000
                  })
                }
              })
          })

        })
        .catch((err) => {
          if(err.code == '120020') {
            weex.requireModule('modal').toast({
              message:'无法获取相机和读写存储权限, 请到系统设置开启',
              duration: 1000
            })
          }else{
            weex.requireModule('modal').toast({
              message:err.message,
              duration: 1000
            })
          }
        })
    }
  }
}
</script>

<style scoped>
    .bg {
        height: 100px;
        background-color: #0f3691;
        align-items: center;
        flex-direction: row;
    }

    .nav-title {
        position: absolute;
        top: 0px;
        left: 100px;
        right: 100px;
        bottom: 0px;
        color: white;
        font-size: 36px;
        text-align: center;
        line-height: 100px;
    }

    .nav-btn {
        position: absolute;
        top: 0px;
        right: 20px;
        bottom: 0px;
        margin-right: 5px;
        font-size: 24px;
        color: white;
        text-align: center;
        line-height: 100px;
    }

    .content {
        flex: 1;
    }

    .cell-category-header-container {
        display: flex;
        flex-direction: row;
        align-items: center;
        background-color: #e8eaf4;
    }

    .cell-category-header-line {
        margin-left: 15px;
        width: 10px;
        height: 40px;
        background-color: rgb(0, 54, 142);
    }

    .cell-category-header-title {
        display: inline-block;
        font-size: 32px;
        color: #0f3691;
        height: 80px;
        line-height: 80px;
        margin-left: 15px;
    }

    .list {
        flex: 1;
        background-color: rgb(249, 249, 249);
    }

    .row {
        width: 750px;
        height: 300px;
        background-color: #ffffff;
        margin-top: 20px;
        flex-direction: row;
    }

    .row-left {
        width: 260px;
        height: 240px;
        margin-top: 30px;
        margin-bottom: 30px;
        margin-left: 20px;
    }

    .course-image {
        width: 260px;
        height: 180px;
        border-radius: 5px;
    }

    .row-left-bottom {
        position: absolute;
        bottom: 0px;
        left: 0px;
        width: 260px;
        height: 65px;
        background-color: #003797;
        margin-bottom: 0px;
    }

    .row-left-bottom-text {
        font-size: 24px;
        color: #ffffff;
        margin-left: 100px;
        margin-top: 25px;
    }

    .row-left-bottom-qr {
        position: absolute;
        bottom: 10px;
        left: 10px;
        width: 80px;
        height: 80px;
        background-color: #ffffff;
        margin-bottom: 0px;
        border-width: 1px;
        border-color: rgb(120, 133, 164);
        border-style: solid;
        align-items: center;
        justify-content: center;
    }

    .qr-image {
        width: 72px;
        height: 72px;
    }

    .row-right {
        width: 410px;
        height: 240px;
        margin-top: 30px;
        margin-bottom: 30px;
        margin-left: 20px;
        justify-content: space-between;
    }

    .row-right-title {
        font-size: 24px;
        color: #000;
    }

    .row-right-title2 {
        font-size: 24px;
        color: #646464;
    }

    .row-right-line3 {
        width: 410px;
        height: 30px;
        flex-direction: row;
    }

    .btn-type {
        width: 60px;
        height: 30px;
        border-radius: 5px;
        background-color: #0f3691;
        font-size: 22px;
        color: #ffffff;
        align-items: center;
        justify-content: center;
    }

    .count-text {
        font-size: 24px;
        color: #969696;
        margin-left: 20px;
        line-height: 30px;
        align-items: center;
    }

    .row-right-line4 {
        width: 410px;
        height: 40px;
        flex-direction: row;
    }

    .blue-btn {
        width: 80px;
        height: 40px;
        border-radius: 5px;
        border-style: solid;
        border-width: 1px;
        border-color: #0f3691;
        background-color: #ffffff;
        align-items: center;
        justify-content: center;
    }

    .blue-btn-margin {
        margin-left: 30px;
    }

    .blue-text {
        font-size: 24px;
        color: #0f3691;
    }
</style>
