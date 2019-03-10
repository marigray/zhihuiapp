<template>
    <div>
        <nav-bar show-back="true" title="智荟精选"></nav-bar>
        <list class="list">
            <!--课程精选-->
            <cell v-for="(item, index) in courseList" :key="index" class="course-cell">
                <div class="course-content" @click="jumpDetail(item.course)">
                    <image class="course-image" resize="cover" :src="item.course.image"></image>
                    <div class="course-info">
                        <text class="course-titlle">{{item.course.name}}</text>
                        <text class="course-learned">{{item.enrollNum}}学过</text>
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

var navigator = weex.requireModule('navigator')

export default {
  components: {NavBar},
  data () {
    return {
      courseList: []
    }
  },
  computed: {},
  created () {
    this.getRecommendationCourse()
  },
  methods: {
    jumpDetail (item) {
      navigator.push({
        url: api.getUri('detail') + '?courseId=' + item.id,
        animated: true
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
    }
  }
}
</script>

<style scoped>
    .list {
        flex: 1;
        background-color: white;
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
