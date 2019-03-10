<template>
    <div class="wrapper" @viewappear="viewappear">
        <nav-bar title="课程列表">
            <text slot="right-icon1" class="coures-add" @click="jumpAddCourse">添加课程</text>
        </nav-bar>
        <list class="list" show-scrollbar=false @loadmore="loadMore">
            <cell v-for="(item,index) in courseList" :key="index">
                <div class="row">
                    <text class="title" lines="1">{{item.courseInfo.name}}</text>
                    <div class="btn-div">
                        <div class="published-btn">
                            <!--<text class="white-text" @click="jumpPublishedCourse()">已发布列表</text>-->
                        </div>
                        <div class="image-btn" @click="onView(item.courseInfo)">
                            <image class="icon" :src="iconPath('icon_eye')"></image>
                            <text class="blue-text">查看</text>
                        </div>
                        <div class="image-btn" @click="onEdit(item.courseInfo)">
                            <image class="icon" :src="iconPath('icon_pen')"></image>
                            <text class="blue-text">编辑</text>
                        </div>
                        <div class="image-btn" @click="deleteCourse(item.courseInfo)">
                            <image class="icon" :src="iconPath('icon_delete')"></image>
                            <text class="blue-text">删除</text>
                        </div>
                        <div class="image-btn" @click="jumpPublish(item.courseInfo)">
                            <image class="icon" :src="iconPath('icon_release')"></image>
                            <text class="blue-text">发布</text>
                        </div>
                    </div>
                </div>
            </cell>
            <cell class="load-cell">
                <more-footer :status="loadingStatus"></more-footer>
            </cell>
        </list>
        <PopupMenu v-if="isPopupShow" @popupClickedWithBtnIndex="popupClickedWithBtnIndex"
                   contentInfo="您确认要删除该课程吗" iconImage="icon_gth" :statusType="true"></PopupMenu>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import user from '@/modules/user'
import api from '@/modules/api'
import PopupMenu from './components/popup-menu'
import PageLoadError from './components/page-load-error'
import MoreFooter from './components/more-footer'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar, PopupMenu, PageLoadError, MoreFooter},
  data () {
    return {
      loadingStatus: 'loading',
      isPopupShow: false,
      courseList: [],
      currentItem: null,
      pageNo: 1
    }
  },
  created () {
    this.getCourseList()
    var globalEvent = weex.requireModule('globalEvent');
    globalEvent.addEventListener('course-changed', function (e) {
      this.reset()
    }.bind(this))
    globalEvent.addEventListener('user-changed', function () {
      this.reset()
    }.bind(this))
  },
  methods: {
    onNavigatorPop () {
      navigator.pop()
    },
    jumpAddCourse () {
      navigator.push({
        url: api.getUri('add1') + '?editable=1',
        animated: true
      })
    },
    jumpPublish (item) {
      navigator.push({
        url: api.getUri('publish') + '?courseId=' + item.id + '&name=' + item.name,
        animated: true
      })
    },
    loadMore () {
      if (this.loadingStatus === 'no-more' || this.loadingStatus === 'no-data') return
      this.pageNo++
      this.getCourseList()
    },
    reset () {
      this.pageNo = 1
      this.courseList = []
      this.loadingStatus = 'loading'
      this.getCourseList()
    },
    getCourseList () {
      let self = this
      api.get('/edu/course/getCoursePageByTeacher',
        {
          id: user.userId(),
          status: 4,
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

        })
    },
    popupClickedWithBtnIndex (index) {
      let arrayIndex = -1
      for (var i = 0; i < this.courseList.length; i++) {
        if (this.courseList[i].courseInfo === this.currentItem) arrayIndex = i
      }
      this.isPopupShow = false
      if (arrayIndex === -1) {
        return
      }

      if (index === 1) {
        let self = this
        api.post('/edu/course/deleteCourse', {uid: user.userId(), id: this.currentItem.id},
          function (data) {
            self.courseList.splice(arrayIndex, 1)
          },
          function (e) {
            weex.requireModule('modal').toast({
              message: e,
              duration: 1000
            })
          })
      }
    },
    deleteCourse (item) {
      this.currentItem = item
      this.isPopupShow = true
    },
    onEdit (item) {
      navigator.push({
        url: api.getUri('add1') + '?courseId=' + item.id + '&type=edit&editable=1',
        animated: true
      })
    },
    onView (item) {
      navigator.push({
        url: api.getUri('add1') + '?courseId=' + item.id + '&type=edit',
        animated: true
      })
    }
  }
}
</script>

<style scoped>
    .coures-add {
        width: 140px;
        height: 50px;
        position: absolute;
        right: 20px;
        top: 25px;
        line-height: 50px;
        text-align: right;
        color: white;
    }

    .list {
        flex: 1;
        background-color: rgb(244, 244, 244);
    }

    .wrapper {
        background-color: rgb(244, 244, 244);
    }

    .row {
        width: 710px;
        margin-left: 20px;
        background-color: white;
        border-radius: 10px;
        margin-top: 20px;
    }

    .title {
        font-size: 28px;
        color: #333333;
        margin-left: 20px;
        margin-top: 20px;
        margin-bottom: 20px;
    }

    .btn-div {
        margin-top: 20px;
        margin-left: 20px;
        margin-right: 20px;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .published-btn {
        width: 150px;
        height: 50px;
        /*border-radius: 5px;*/
        /*border-style: solid;*/
        /*border-width: 1px;*/
        /*background-color: #0f3691;*/
        justify-content: center;
        align-items: center;
    }

    .white-text {
        font-size: 24px;
        color: #ffffff;
    }

    .image-btn {
        width: 100px;
        height: 50px;
        border-radius: 5px;
        border-style: solid;
        border-width: 1px;
        border-color: rgb(15, 54, 145);
        background-color: rgb(255, 255, 255);
        flex-direction: row;
        justify-content: center;
        align-items: center;
    }

    .icon {
        width: 30px;
        height: 30px;
    }

    .blue-text {
        font-size: 24px;
        color: #0f3691;
    }

    .nodata {
        flex: 1;
    }
</style>
