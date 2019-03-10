<template>
    <div class="bg">
        <navbar show-back="true" title="课程处理"></navbar>
        <div class="content">
            <div class='cell-category-header-container'>
                <div class='cell-category-header-line'></div>
                <text class='cell-category-header-title'>{{navTitle}}</text>
            </div>
            <list class="list" show-scrollbar=false @loadmore="loadMore">
                <cell v-for="(item,index) in courseList" :key="index">
                    <div class="row">
                        <div class="row-left">
                            <div class="row-left-bottom">
                                <text class="row-left-bottom-text">课程二维码</text>
                            </div>
                            <image class="course-image" :src="item.courseInfo.image">
                            </image>
                            <div class="row-left-bottom-qr">
                                <image class="qr-image" :src=item.QRcode>
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
                            <div class="row-right-line4" v-if="status == 3">
                                <div class="blue-btn" @click="jumpSurvey()">
                                    <text class="blue-text">问卷</text>
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
        <!--&lt;!&ndash;弹框&ndash;&gt;-->
        <!--<div v-if="showBox">-->
        <!--<div class="mask-layer"></div>-->
        <!--<div class="mask-window">-->
        <!--<div class="mask-window-title">-->
        <!--<text class="window-title">调整上课时间</text>-->
        <!--<image class="icon" :src="iconPath('icon_close')" @click="closeWindow"></image>-->
        <!--</div>-->
        <!--<div class="popup-content">-->
        <!--<div class="detail">-->
        <!--<text class="select-txt">选择日期</text>-->
        <!--<input class="input" type="date" @input=""></input>-->
        <!--<image class="icon" :src="iconPath('icon_date')"></image>-->
        <!--</div>-->
        <!--<div class="detail">-->
        <!--<text class="select-txt">选择开始时间</text>-->
        <!--<text class="button" @click="pickTime"></text>-->
        <!--<image class="icon" :src="iconPath('icon_time')"></image>-->
        <!--</div>-->
        <!--<div class="detail">-->
        <!--<text class="select-txt">选择结束时间</text>-->
        <!--<text class="button" @click="pickTime"></text>-->
        <!--<image class="icon" :src="iconPath('icon_time')"></image>-->
        <!--</div>-->
        <!--<div class="btn-container">-->
        <!--<text class="cancel" @click="closeWindow">取消</text>-->
        <!--<div class="gapDiv"></div>-->
        <!--<text class="submit" @click="">确认</text>-->
        <!--</div>-->
        <!--</div>-->
        <!--</div>-->
        <!--</div>-->
    </div>
</template>

<script>
import navbar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import user from '@/modules/user'
import api from '@/modules/api'
import MoreFooter from './components/more-footer'

var navigator = weex.requireModule('navigator')
// const picker = weex.requireModule('picker')
export default {
  mixins: [mixin],
  components: {navbar, MoreFooter},
  data () {
    return {
      loadingStatus: 'loading',
      courseList: [],
      showBox: true,
      pageNo: 1,
      status: 3
    }
  },
  created () {
    this.getCourseList()
  },
  computed: {
    navTitle () {
      if (this.status === '1') {
        return '已发布的课程'
      }
      if (this.status === '3') {
        return '历史课程'
      }
      return ''
    }
  },
  methods: {
    loadMore () {
      if (this.loadingStatus === 'no-more' || this.loadingStatus === 'no-data') return
      this.pageNo++
      this.getCourseList()
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
    getCourseList () {
      let self = this
      api.get('/edu/course/getCoursePageByTeacher',
        {
          id: user.userId(),
          status: this.status,
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
    }
  }
}
</script>

<style scoped>
    .bg {
        flex: 1;
        background-color: #0f3691;
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

    .mask-layer {
        width: 970px;
        height: 1800px;
        background-color: #000;
        align-items: center;
        opacity: 0.5;
        flex-direction: row;
        z-index: 960;
        top: 0;
        left: 0;
        position: fixed;
    }

    .mask-window {
        position: fixed;
        top: 300px;
        left: 20px;
        width: 710px;
        height: 560px;
        border: 1px solid #C6C6C6;
        border-radius: 10px;
        z-index: 970;
        background-color: white;
    }

    .popup-content {
        align-items: center;
    }

    .icon {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 20px;
        right: 20px;
        z-index: 1000;
    }

    .btn-container {
        margin-top: 50px;
        flex-direction: row;
    }

    .cancel {
        width: 280px;
        background-color: rgb(230, 230, 230);
        height: 80px;
        text-align: center;
        line-height: 80px;
        color: rgb(88, 88, 88);
        font-size: 30px;
        border-radius: 10px;
    }

    .gapDiv {
        width: 30px;
    }

    .submit {
        background-color: rgb(6, 58, 149);
        width: 280px;
        height: 80px;
        color: white;
        text-align: center;
        line-height: 80px;
        font-size: 30px;
        border-radius: 10px;
    }

    .window-title {
        text-align: center;
        font-size: 32px;
        color: #333;
        margin-top: 28px;
    }

    .detail {
        width: 660px;
        height: 80px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        flex-direction: row;
        align-items: center;
        padding-left: 20px;
        padding-right: 20px;
        margin-top: 20px;
    }

    .select-txt {
        font-size: 24px;
        color: #333333;
        margin-left: 20px;
    }

    .icon {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 20px;
        right: 20px;
    }

    .mask-window-title {
        height: 100px;
        vertical-align: middle;
    }

    .input, .button {
        font-size: 24px;
        height: 50px;
        color: #333333;
        flex: 1;
        margin: 20px;
        margin-left: 50px;
        margin-right: 50px;
        text-align: right;
    }
</style>
