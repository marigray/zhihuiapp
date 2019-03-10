<template>
    <waterfall class='list' column-count=2 column-gap=6>
        <cell class="cell" v-for="(item,index) in recommendList" :key="index" @click="onDetail(item)">
            <div class="cell-content">
                <image class="cell-course-img" :src="item.image"></image>
                <text style="font-size:28px;color:#333333;margin-top:20px">{{item.name}}</text>
                <div class="cell-course-line">
                    <div class="btn-type">
                        <text style="font-size:24px;color:#ffffff;">{{CourseTypeString(item.type)}}</text>
                    </div>
                    <text style="font-size:24px;color:#969696;margin-left:20px">{{item.count}}</text>
                </div>
                <text style="font-size:28px;color:#ff3c2d">{{item.price}}</text>
            </div>
        </cell>
        <header>
            <more-footer class="load-cell" :status="loadingStatus"></more-footer>
        </header>
    </waterfall>
</template>

<script>
import api from '@/modules/api'
import user from '@/modules/user'
import mixin from '@/modules/mixin'
import PageLoadError from './components/page-load-error'
import MoreFooter from './components/more-footer'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {PageLoadError, MoreFooter},
  name: 'course-category-list',
  props: ['industryId', 'functionId', 'type'],
  data () {
    return {
      loadingStatus: 'loading',
      bannerList: [
        'http://img02.tooopen.com/images/20150928/tooopen_sy_143912755726.jpg',
        'http://img06.tooopen.com/images/20160818/tooopen_sy_175866434296.jpg',
        'http://img06.tooopen.com/images/20160818/tooopen_sy_175833047715.jpg'
      ],
      recommendList: [],
      pageNo: 1
    }
  },
  created () {
    this.getCourseList()
  },
  computed: {
  },
  methods: {
    getCourseList () {
      let self = this
      api.get('/edu/course/getCoursePageByIndustryAndFunction',
        {
          industryid: this.industryId,
          functionid: this.functionId,
          type: this.type,
          pageNo: this.pageNo,
          pageCount: 10
        },
        function (data) {
          self.recommendList = self.recommendList.concat(data.content.records)
          if (self.recommendList.length === 0) {
            self.loadingStatus = 'no-data'
          } else {
            if (data.content.total === self.recommendList.length) {
              self.loadingStatus = 'no-more'
            }
          }
        },
        function () {
          self.loadingStatus = 'error'
        }
      )
    },
    onDetail (item) {
      navigator.push({
        url: api.getUri('detail') + '?courseId=' + item.id,
        animated: true
      })
    }
  }
}
</script>

<style scoped>
    .wrapper {
        flex: 1;
    }

    .list {
        width: 750px;
        flex: 1;
        background-color: rgb(240, 240, 240);
    }

    .load-cell {
        height: 50px;
        width: 750px;
    }

    .cell {
    }

    .cell-content {
        width: 360px;
        height: 405px;
        padding: 10px;
        background-color: white;
        border-radius: 10px;
    }

    .cell-course-img {
        width: 350px;
        height: 220px;
        border-radius: 5px;
        background-color: #d2d2d2;
    }

    .cell-course-line {
        width: 320px;
        height: 80px;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .btn-type {
        width: 80px;
        height: 40px;
        border-radius: 5px;
        background-color: #0f3691;
        font-size: 22px;
        color: #ffffff;
        align-items: center;
        justify-content: center;
    }
</style>
