<template>
    <div>
        <nav-bar title="详情" show-back="true" @rightIcon1Clicked="onComment" @rightIcon2Clicked="onShare">
            <image slot="right-icon2" class="share-btn" :style="{right: !isEnroll? '20px':'100px'}"
                   :src="iconPath('share_white')" @click="onShare"></image>
            <image v-if="isEnroll" slot="right-icon1" class="comment-btn" :src="iconPath('edit_white')"
                   @click="onComment"></image>
        </nav-bar>

        <list v-if="detailInfo" @loadmore="onLoadMoreComments">
            <cell class="video-image">
                <image :src="detailInfo.image" class="video-image"></image>
                <div class="play-btn-layer" v-if="detailInfo.type===1">
                    <image class="play-btn" :src="iconPath('banner_video')" @click="getVideoURL"></image>
                </div>
            </cell>
            <cell class='tab-view'>
                <div class='tab-btn' v-for="(item,index) in tablist" :key="index" @click="onTabClicked(index)">
                    <text :class="['tab-text', tabIndex==index?'tab-text-selected':'']">{{item}}</text>
                    <div :class="['tab-line', tabIndex==index?'tab-line-selected':'']"></div>
                </div>
            </cell>
            <cell class='intro' v-if="tabIndex==0">
                <text class='course-title'>{{detailInfo.name}}</text>
                <div class='course-type-container'>
                    <text class='course-type'>{{CourseTypeString(detailInfo.type)}}</text>
                    <text class='course-learned'>已有{{enrollNum}}人学过</text>
                    <div class="course-gap"></div>
                    <text class="course-time" v-if="detailInfo.type==2">上课时间：{{formatDate(detailInfo.startDate)}}</text>
                </div>
                <text class='course-price'>{{detailInfo.price ? detailInfo.price : '免费'}}</text>
                <div class='line-gap'></div>
                <div v-if="detailInfo.type===2">
                    <cell-category-header title="课程学习"></cell-category-header>
                    <text class='bg-text'>{{detailInfo.address}}</text>
                    <div class='line-gap'></div>
                </div>
                <cell-category-header title="课程讲师"></cell-category-header>
                <div v-if="teacherInfo">
                    <div class="teacher-top">
                        <image class="teacher-header" @click="onTeacher" :src="teacherInfo.headimgurl"></image>
                        <div class="teacher-info">
                            <text class="teacher-name">{{teacherInfo.name}}</text>
                            <text class="teacher-focus">{{collectionNum}}人关注</text>
                        </div>
                        <div class="teacher-gap"></div>
                        <div class="focus-btn" @click="onFocus">
                            <image class="focus-image" :src="focusImageURL"></image>
                            <text :class="['teacher-focus', isFocused?'teacher-focus-selected':'']">关注</text>
                        </div>
                    </div>
                    <div class="teacher-des">
                        <text class="teacher-des-text">{{tintroduction}}
                        </text>
                    </div>
                </div>
                <div class='line-gap'></div>

                <cell-category-header title="课程背景"></cell-category-header>
                <text class='bg-text'>{{detailInfo.background}}</text>
                <div class='line-gap'></div>

                <cell-category-header title="其它介绍"></cell-category-header>
                <text class='target-text'>{{detailInfo.target}}</text>

                <cell-category-header title="课程受众"></cell-category-header>
                <text class='audiences-text'>{{detailInfo.audiences}}</text>
                <div class='line-gap'></div>

                <cell-category-header title="学习收益"></cell-category-header>
                <text class='target-text'>{{detailInfo.gains}}</text>
            </cell>

            <cell class='picture-info' v-if="tabIndex==1">
                <cell-category-header title="图文详情"></cell-category-header>
                <image class='detail-image' v-for="(item,index) in introduction" :key="index"
                       :src="item.url" :style="{height: item.height/item.width*750.0}">
                </image>
            </cell>
            <cell class="picture-info" v-if="tabIndex==2">
                <cell-category-header title="全部评价"></cell-category-header>
            </cell>
            <cell class='comment-cell' v-if="tabIndex==2" v-for="(item,index) in comments" :key="index"
                  @click="onCommentDetail(item)">
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
            <cell class="load-cell" v-if="tabIndex==2">
                <more-footer :status="loadingStatus"></more-footer>
            </cell>
        </list>
        <div class="bottom" v-if="detailInfo && (!hidePay && !isEnroll) && parseInt(detailInfo.type)!==2">
            <div class="left-bottom" @click="tryPlay">
                <image class="try-btn" :src="iconPath('foot_video')"></image>
                <text class="try-title">试看</text>
            </div>
            <text class="buy-btn" @click="pay">购买 {{detailInfo.price ? detailInfo.price : '免费'}}</text>
        </div>
        <comment-dialog @commentSubmit="commentSubmit" title="评论" @commentCancel="commentCancel"
                        v-if="showCommentDialog"></comment-dialog>

        <share v-if="showShare" @shareClose="shareClose" :shareParams="courseParams"></share>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import CellCategoryHeader from './components/cell-category-header.vue'
import CommentDialog from './components/CommentDialog'
import api from '@/modules/api'
import user from '@/modules/user'
import mixin from '@/modules/mixin'
import PageLoadError from './components/page-load-error'
import Share from './components/share'
import MoreFooter from './components/more-footer'

var navigator = weex.requireModule('navigator')

export default {
    mixins: [mixin],
    components: {NavBar, CellCategoryHeader, CommentDialog, PageLoadError, Share, MoreFooter},
    data() {
        return {
            courseId: 37,
            tabIndex: 0,
            detailInfo: null,
            introduction: [],
            tablist: ['介绍', '图文', '评价'],
            comments: [],
            teacherInfo: null,
            isFocused: false,
            showCommentDialog: false,
            tintroduction: null,
            showShare: false,
            videoURL: null,
            readToPlay: false,
            hidePay: false,
            isEnroll: false,
            pageNo: 1,
            loadingStatus: 'loading',
            enrollNum: 0,
            collectionNum: 0,
            courseParams: {}
        }
    },
    created() {
        this.getDetail()
        this.getComments()
        this.getTeacherInfo()
        var globalEvent = weex.requireModule('globalEvent');
        globalEvent.addEventListener('pay-result', function (e) {
            let cid = e.result.courseid
            let result = e.result.result
            if (result && cid === this.courseId) {
                this.isEnroll = true
            }
        }.bind(this))
    },
    computed: {
        focusImageURL() {
            if (this.isFocused) return this.iconPath('icon_fav_o')
            return this.iconPath('icon_fav')
        }
    },
    methods: {
        onTeacher() {
            navigator.push({
                url: api.getUri('teacher') + '?id=' + this.teacherInfo.id,
                animated: true
            })
        },
        onComment() {
            this.showCommentDialog = true
        },
        commentCancel() {
            this.showCommentDialog = false
        },
        onTabClicked(index) {
            this.tabIndex = index
        },
        getDetail() {
            let self = this
            api.get('/edu/course/getCourseInfoById', {id: 268, uid: 192},
              function (data) {
                  self.detailInfo = data.content.course
                  self.introduction = data.content.introduction
                  self.isEnroll = data.content.isEnroll
                  self.enrollNum = data.content.enrollNum
                  self.collectionNum = data.content.collectionNum
              },
              function () {

              })
        },
        onLoadMoreComments() {
            if (this.tabIndex !== 2) return
            if (this.loadingStatus === 'no-more' || this.loadingStatus === 'no-data') return
            this.pageNo++
            this.getComments()
        },
        getComments() {
            let self = this
            api.get('/edu/comment/getCommenPageByCourseNew', {cid: this.courseId, pageNo: this.pageNo, pageCount: 10},
              function (data) {
                  for (let i = 0; i < data.content.records.length; i++) {
                      let item = data.content.records[i]
                      self.comments.push(item)
                  }
                  // self.comments = self.comments.concat(data.content.records)
                  if (self.comments.length === 0) {
                      self.loadingStatus = 'no-data'
                  } else {
                      if (data.content.total === self.comments.length) {
                          self.loadingStatus = 'no-more'
                      }
                  }
              },
              function () {

              })
        },
        resetComments() {
            this.loadingStatus = 'loading'
            this.pageNo = 1
            this.comments = []
            this.getComments()
        },
        getTeacherInfo() {
            let self = this
            api.get('/edu/course/getTeacherInfoByCourse', {cid: this.courseId},
              function (data) {
                  self.teacherInfo = data.content.userInfo
                  if (data.content.userExtra) {
                      self.tintroduction = data.content.userExtra.introduction
                  }
                  self.getFocusStatus()
              },
              function () {

              })
        },
        onFocus() {
            if (!this.isLogined()) {
                this.pushLogin()
                return
            }
            if (!this.isFocused) {
                let self = this
                api.post('/edu/collection/addCollection', {
                      collectionid: self.teacherInfo.id,
                      uid: user.userId(),
                      type: 2
                  },
                  function (data) {
                      self.isFocused = true
                      weex.requireModule('modal').toast({
                          message: '关注成功',
                          duration: 1
                      })
                  },
                  function () {

                  })
            } else {
                let self = this
                api.post('/edu/collection/delCollection', {id: self.teacherInfo.id, uid: user.userId(), type: 2},
                  function (data) {
                      self.isFocused = true
                      weex.requireModule('modal').toast({
                          message: '已取消关注',
                          duration: 1
                      })
                  },
                  function (e) {
                      weex.requireModule('modal').toast({
                          message: e,
                          duration: 1
                      })
                  })
            }
        },
        getFocusStatus() {
            if (!this.isLogined()) return
            let self = this
            api.get('/edu/collection/isCollection', {collectionid: this.teacherInfo.id, uid: user.userId(), type: 2},
              function (data) {
                  self.isFocused = data.content
              },
              function () {

              })
        },
        commentSubmit(e) {
            if (!this.isLogined()) {
                this.pushLogin()
                return
            }
            let self = this
            api.post('/edu/comment/addComment', {cid: this.courseId, uid: user.userId(), content: e},
              function (data) {
                  weex.requireModule('modal').toast({
                      message: '评论成功',
                      duration: 1
                  })
                  self.showCommentDialog = false
                  self.resetComments()
              },
              function (e) {
                  weex.requireModule('modal').toast({
                      message: e.msg,
                      duration: 1
                  })
              })
        },
        onShare() {
            this.showShare = true
            this.courseParams = {
                image: this.detailInfo.image,
                name: this.detailInfo.name,
                background: this.detailInfo.background,
                audiences: this.detailInfo.audiences,
                gains: this.detailInfo.gains,
                other: this.detailInfo.target,
                headimgurl: this.teacherInfo.headimgurl,
                teachername: this.teacherInfo.name,
                price: this.detailInfo.price,
                tintroduction: this.teacherInfo.label,
                time: this.formatDate(this.detailInfo.startDate / 1000),
                area: this.detailInfo.city,
                address: this.detailInfo.address,
                orcodeurl: this.detailInfo.qrcode,
            }
            //   weex.requireModule('share').saveCoursePic(
            //     {
            //       image: this.detailInfo.image,
            //       name: this.detailInfo.name,
            //       background: this.detailInfo.background,
            //       audiences: this.detailInfo.audiences,
            //       gains: this.detailInfo.gains,
            //       other: this.detailInfo.target,
            //       headimgurl: this.teacherInfo.headimgurl,
            //       teachername: this.teacherInfo.name,
            //       price: this.detailInfo.price,
            //       tintroduction: this.teacherInfo.label,
            //       time: this.formatDate(this.detailInfo.startDate / 1000),
            //       area: this.detailInfo.city,
            //       address: this.detailInfo.address,
            //       orcodeurl: this.detailInfo.qrcode,
            //       wxscene: 0
            //     })
        },
        shareClose() {
            this.showShare = false
        },
        getVideoURL() {
            if (!this.isEnroll) {
                this.pay()
                return
            }

            let self = this
            api.get('/edu/video/getRealDownloadUrl', {downloadUrl: this.detailInfo.video},
              function (data) {
                  self.videoURL = data.content
                  weex.requireModule('file').playVideo({url: self.videoURL, playall: true})
              },
              function () {

              })
        },
        onCommentDetail(item) {
            navigator.push({
                url: api.getUri('course-comments-detail') + '?courseId=' + this.courseId + '&pid=' + item.comment.id,
                animated: true
            })
        },
        tryPlay() {
            let self = this
            api.get('/edu/video/getRealDownloadUrl', {downloadUrl: this.detailInfo.video},
              function (data) {
                  self.videoURL = data.content
                  weex.requireModule('file').playVideo({url: self.videoURL, playall: false})
              },
              function () {

              })
        },
        pay() {
            if (!this.isLogined()) {
                this.pushLogin()
                return
            }
            if (this.isEnroll) {
                return
            }
            // 生成订单号
            let self = this
            api.post('/edu/order/addOrder', {cid: this.courseId, userid: user.userId(), price: this.detailInfo.price},
              function (data) {
                  let orderId = data.content
                  // 免费直接调用
                  if (!self.detailInfo.price || self.detailInfo.price === 0 || self.detailInfo.price === '0') {
                      api.post('/edu/wechat/unifiedorder',
                        {
                            cid: self.courseId,
                            uid: user.userId(),
                            totalFee: 0,
                            body: self.detailInfo.name,
                            billno: orderId
                        },
                        function (data) {
                            weex.requireModule('modal').toast({
                                message: '课程报名成功',
                                duration: 1000
                            })
                            weex.requireModule('myevent').sendGlobal('pay-result', {
                                result: {
                                    cid: self.courseId,
                                    result: true
                                }
                            })
                            self.isEnroll = true
                        },
                        function (e) {
                            weex.requireModule('modal').toast({
                                message: '课程报名失败',
                                duration: 1000
                            })
                        })
                      return
                  }
                  navigator.push({
                      url: api.getUri('order') + '?orderId=' + orderId + '&courseId=' + self.courseId,
                      animated: true
                  })
              },
              function (e) {
                  weex.requireModule('modal').toast({
                      message: e.msg,
                      duration: 1000
                  })
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

    .share-btn {
        width: 50px;
        height: 50px;
        position: absolute;
        right: 100px;
        top: 25px;
    }

    .tab-view {
        width: 750px;
        height: 80px;
        background-color: rgb(245, 245, 245);
        display: flex;
        flex-direction: row;
    }

    .video-image {
        width: 750px;
        height: 400px;
        background-color: rgba(0, 0, 0, 0.5);
    }

    .tab-btn {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }

    .tab-text {
        margin-top: 20px;
        font-size: 28px;
    }

    .tab-text-selected {
        color: #0f3691;
    }

    .tab-line {
        margin-top: 20px;
        width: 80px;
        height: 5px;
        background-color: #003797;
        opacity: 0;
    }

    .teacher-focus-selected {
        color: #0f3691;
    }

    .tab-line-selected {
        opacity: 1;
    }

    .intro,
    .picture-info {
        display: flex;
        flex-direction: column;
        padding: 20px;
    }

    .course-title {
        margin-top: 20px;
        font-size: 38px;
        color: #333;
    }

    .course-type-container {
        display: flex;
        flex-direction: row;
        height: 80px;
        align-items: center;
    }

    .course-type {
        font-size: 24px;
        color: white;
        width: 80px;
        height: 50px;
        text-align: center;
        line-height: 50px;
        border-radius: 10px;
        background-color: #0f3691;
    }

    .course-learned {
        margin-left: 20px;
        font-size: 28px;
        color: #969696;
    }

    .course-gap {
        display: flex;
        flex: 1;
    }

    .course-time {
        margin-right: 20px;
        font-size: 28px;
        color: #969696;
    }

    .course-price {
        font-size: 38px;
        color: #ff3c2d;
        margin-bottom: 25px;
    }

    .line-gap {
        width: 710px;
        height: 10px;
        background-color: #f0f0f0;
    }

    .bg-text,
    .target-text,
    .audiences-text {
        margin-left: 30px;
        margin-right: 30px;
        margin-bottom: 30px;
        font-size: 24px;
        color: #646464;
    }

    .detail-image {
        margin-left: 10px;
        margin-bottom: 20px;
        width: 690px;
        height: 400px;
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
        flex: 1;
    }

    .comment-date {
        color: #646464;
        font-size: 24px;
        margin-right: 20px;
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

    .teacher-top {
        margin-top: 15px;
        height: 110px;
        width: 710px;
        margin-bottom: 15px;
        flex-direction: row;
        align-items: center;
    }

    .teacher-header {
        width: 100px;
        height: 100px;
        left: 20px;
        border-radius: 50px;
    }

    .focus-btn {
        width: 120px;
        height: 50px;
        background-color: #e6ebf8;
        flex-direction: row;
        align-items: center;
        justify-content: center;
        border-radius: 5px;
    }

    .teacher-info {
        margin-left: 30px;
    }

    .teacher-name {
        font-size: 28px;
        color: #333333;
    }

    .focus-image {
        width: 30px;
        height: 30px;
    }

    .teacher-focus {
        margin-left: 10px;
        font-size: 24px;
        color: #969696;
    }

    .teacher-gap {
        flex: 1;
    }

    .teacher-des {
        padding: 20px;
        padding-top: 0px;
    }

    .teacher-des-text {
        font-size: 24px;
        color: #646464;
    }

    .no-data {
        height: 700px;
        width: 750px;
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

    .video {
        left: 0px;
        top: 0px;
        bottom: 0px;
        right: 0px;
        position: absolute;
        width: 750px;
        height: 1000px;
    }

    .bottom {
        height: 80px;
        width: 750px;
        flex-direction: row;
        position: fixed;
        bottom:0;
        left: 0;
        right: 0;
        background-color: #fff;
    }

    .buy-btn {
        width: 340px;
        height: 80px;
        background-color: #0f3691;
        line-height: 80px;
        text-align: center;
        font-size: 30px;
        color: white;
    }

    .left-bottom {
        height: 80px;
        flex: 1;
        flex-direction: row;
        align-items: center;
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

    .try-btn {
        margin-left: 120px;
        width: 40px;
        height: 40px;
    }

    .try-title {
        margin-left: 20px;
        font-size: 32px;
    }

</style>
