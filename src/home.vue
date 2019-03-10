<template>
    <div @viewappear="viewappear">
        <nav-bar title="智荟"></nav-bar>
        <list class="list">
            <cell>
                <slider class="slider">
                    <div class="poster" v-for="(item, index) in bannerList" v-bind:key="index"
                         @click="jumpDetail(item)">
                        <image class="poster_image" resize="cover" :src="item.image"></image>
                    </div>
                    <indicator class="indicator"></indicator>
                </slider>
            </cell>
            <!--消息-->
            <cell class="message-cell">
                <div class="message-content">
                    <div class="message-tab">
                        <div class="message-tab-left">
                            <div class="message-tab-item" @click="platformMsg">
                                <text :class="['message-tab-text', (messageTabIndex==0)?'message-tab-text-selected':'']">平台通知</text>
                                <div class="message-tab-line" v-if="messageTabIndex==0"></div>
                            </div>
                            <div class="message-tab-item" @click="personalMsg">
                                <text :class="['message-tab-text', (messageTabIndex==1)?'message-tab-text-selected':'']">我的私信</text>
                                <div class="message-tab-line" v-if="messageTabIndex==1"></div>
                            </div>
                        </div>
                        <text class="more-text" @click="jumpMsg">更多 ></text>
                    </div>
                    <div class="message-content-line"></div>
                    <div class="message-bottom" @click="jumpMsg" v-if="messageTabIndex==0 && systemMessage">
                        <text class="message-text">{{systemMessage.message.content}}</text>
                    </div>
                    <div class="message-bottom" @click="jumpMsg" v-if="messageTabIndex==1 && privateMessage">
                        <text class="message-text">{{privateMessage.message.content}}</text>
                    </div>
                </div>
            </cell>
            <!--推荐教师-->
            <cell v-for="(item, index) in recommendTeachers" :key="index">
                <div class="teacher-cell" @click="jumpTeacher(item)">
                    <image :src="item.headimgurl" class="teacher-header"></image>
                    <div class="right-content">
                        <div class="teacher-row1">
                            <text class="teacher-name">{{item.name}}</text>
                        </div>
                        <div class="teacher-row2">
                            <text class="teacher-intro">{{item.grade}}</text>
                        </div>
                        <!--<div class="teacher-row3">-->
                        <!--<text class="teacher-type">线上</text>-->
                        <!--<text class="teacher-learned">已有{{item.learned}}学过</text>-->
                        <!--</div>-->
                    </div>
                </div>
                <div class="teacher-cell-line"></div>
            </cell>

            <!--课程精选-->
            <cell v-for="(item, index) in courseList" :key="index" class="course-cell">
                <div class="course-content" @click="jumpDetail(item.course)">
                    <image class="course-image" resize="cover" :src="item.course.image"></image>
                    <div class="course-info">
                        <text class="course-titlle">{{item.course.name}}</text>
                        <text class="course-learned">{{item.enrollNum}}人学过</text>
                        <div class="course-info-gap"></div>
                        <text class="course-price">{{item.course.price?'￥'+item.course.price:'免费'}}</text>
                    </div>
                </div>
            </cell>
        </list>
    </div>
</template>

<script>

import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import user from '@/modules/user'

var navigator = weex.requireModule('navigator')

export default {
  components: {NavBar},
  data () {
    return {
      messageTabIndex: 0,
      bannerList: [],
      systemMessage: null,
      privateMessage: null,
      recommendTeachers: [],
      courseList: []
    }
  },
  computed: {},
  created () {
    this.getBanners()
    this.getRecommendationCourse()
    this.getRecommendationTeacher()
    this.getSystemMessage()
    this.getPrivateMessage()
  },
  methods: {
    viewappear () {
      this.getSystemMessage()
      this.getPrivateMessage()
    },
    messageTabClass (index) {
      let array = ['message-tab-text']
      if (index === this.messageTabIndex) array.push('message-tab-text-selected')
      return array
    },
    jumpDetail (item) {
      navigator.push({
        url: api.getUri('detail') + '?courseId=' + item.id,
        animated: true
      })
    },
    platformMsg () {
      this.messageTabIndex = 0
    },
    personalMsg () {
      this.messageTabIndex = 1
    },
    jumpMsg () {
      navigator.push({
        url: api.getUri('message'),
        animated: true
      })
    },
    jumpTeacher (item) {
      navigator.push({
        url: api.getUri('teacher') + '?id=' + item.id,
        animated: true
      })
    },
    getBanners () {
      let self = this
      api.get('/edu/carousel/getCarouselList', null,
        function (data) {
          self.bannerList = data.content
        },
        function () {

        })
    },
    getRecommendationCourse () {
      let self = this
      api.get('/edu/recommendation/getRecommendationCourseList', null,
        function (data) {
          self.courseList = data.content
        },
        function () {

        })
    },
    getRecommendationTeacher () {
      let self = this
      api.get('/edu/recommendation/getRecommendationTeacherList', null,
        function (data) {
          self.recommendTeachers = data.content
        },
        function () {

        })
    },
    getSystemMessage () {
      let self = this
      api.get('/edu/message/getSystemMessage', {type: 1, userid: user.userId()},
        function (data) {
          if (data.content.records && data.content.records.length) {
            self.systemMessage = data.content.records[0]
          } else {
            self.systemMessage = {message: {content: '暂无相关消息'}}
          }
        },
        function () {

        })
    },
    getPrivateMessage () {
        if (!this.isLogined()) {
            self.privateMessage = {message: {content: '暂无相关消息'}}
            return;
        }
      let self = this
      api.get('/edu/message/getSystemMessage', {type: 3, userid: user.userId()},
        function (data) {
          if (data.content.records && data.content.records.length) {
            self.privateMessage = data.content.records[0]
          } else {
            self.privateMessage = {message: {content: '暂无相关消息'}}
          }
        },
        function () {

        })
    }
  }
}

</script>

<style scoped>
    .list {
        flex: 1;
        background-color: white;
    }

    .slider {
        width: 750px;
        height: 420px;
    }

    .indicator {
        position: absolute;
        margin-top: 390px;
        width: 200px;
        height: 20px;
        left: 275px;
    }

    .poster {
        width: 750px;
        height: 420px;
    }

    .poster_image {
        margin: 20px;
        width: 710px;
        height: 400px;
        border-radius: 10px;
    }

    .message-cell {
        align-items: center;
        background-color: rgb(249, 249, 249);
    }

    .message-content {
        margin-top: 20px;
        margin-bottom: 20px;
        width: 710px;
        background-color: white;
        height: 200px;
        border-radius: 10px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
    }

    .message-tab {
        flex-direction: row;
        height: 80px;
        width: 710px;
        justify-content: space-between;
        align-items: center;
    }

    .message-tab-left {
        flex-direction: row;
    }

    .message-tab-item {
        margin-left: 20px;
        margin-right: 10px;
    }

    .message-tab-line {
        height: 4px;
        width: 100%;
        background-color: #0f3691;
    }

    .message-content-line {
        height: 2px;
        background-color: #dcdcdc;
    }

    .message-bottom {
        padding: 20px;
    }

    .more-text {
        font-size: 24px;
        color: #0f3691;
        margin-right: 20px;
    }

    .message-type {
        position: absolute;
        top: 20px;
        left: 20px;
        color: white;
        padding: 0px 8px;
        border-radius: 3px;
        width: 90px;
        height: 30px;
        line-height: 30px;
        background-color: rgb(15, 54, 145);
        font-size: 18px;
    }

    .message-tab-text {
        height: 76px;
        line-height: 76px;
        font-size: 24px;
    }

    .message-tab-text-selected {
        color: #0f3691;
    }

    .message-text {
        font-size: 24px;
        color: #646464;
    }

    .teacher-cell {
        width: 750px;
        height: 190px;
        flex-direction: row;
    }

    .teacher-header {
        margin-top: 40px;
        margin-left: 20px;
        margin-bottom: 30px;
        width: 120px;
        height: 120px;
        border-radius: 60px;
    }

    .right-content {
        margin-right: 20px;
        flex: 1;
        justify-content: center;
    }

    .teacher-row1 {
        margin-top: 40px;
        margin-left: 20px;
        flex-direction: row;
        align-items: center;
    }

    .teacher-name {
        font-size: 24px;
        color: #0f3691;
    }

    .teacher-title {
        margin-left: 20px;
        font-size: 22px;
        color: #969696;
    }

    .teacher-row2 {
        margin-top: 10px;
        margin-left: 20px;
        flex-direction: row;
        align-items: center;
    }

    .teacher-intro {
        font-size: 28px;
        color: #333333;
    }

    .teacher-row3 {
        height: 35px;
        margin-top: 10px;
        margin-left: 20px;
        flex-direction: row;
        align-items: center;
    }

    .teacher-type {
        color: white;
        padding: 0px 8px;
        border-radius: 5px;
        height: 35px;
        line-height: 35px;
        background-color: #0f3691;
        font-size: 18px;
    }

    .teacher-learned {
        height: 35px;
        line-height: 35px;
        margin-left: 10px;
        font-size: 24px;
        color: #969696;
    }

    .teacher-cell-line {
        display: block;
        margin-left: 20px;
        width: 730px;
        height: 2px;
        background-color: #d2d2d2;
    }

    .course-cell {
        background-color: rgb(249, 249, 249);
    }

    .course-content {
        margin: 30px 20px;
        background-color: white;
        width: 710px;
        height: 300px;
        border-radius: 10px;
        /*border-color: lightgray;*/
        /*border-width: 1px;*/
    }

    .course-image {
        width: 710px;
        height: 240px;
    }

    .course-info {
        height: 60px;
        flex-direction: row;
        align-items: center;
    }

    .course-titlle {
        margin-left: 20px;
        font-size: 24px;
        color: #333333;
    }

    .course-info-gap {
        flex: 1;
    }

    .course-learned {
        margin-left: 10px;
        font-size: 24px;
        color: #969696;
    }

    .course-price {
        font-size: 28px;
        margin-right: 20px;
        color: #ec5f59;
    }
</style>
