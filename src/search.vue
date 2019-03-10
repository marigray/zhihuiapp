<template>
    <div>
        <navbar show-back="true" title="搜索课程">
            <text slot="right-icon1" class="cancel-btn" v-if="!showCategory" @click="cancel">取消</text>
        </navbar>
        <div class='search-bg'>
            <div class='search-bg-white'>
                <input class="search-bg-input"
                       @input="searchContentInput" placeholder="请输入课程名称,老师"
                       @change="searchCourseOrTeacher" @return="searchCourseOrTeacher"></input>
            </div>
        </div>
        <div v-if="showCategory">
            <div class="search-option">
                <div class="option-item" @click="toggleSelected(1)">
                    <image class="optionIcon" resize="cover"
                           :src="choice===1?chooseOption.selectIcon:chooseOption.icon"></image>
                    <text class="optionSpan">课程</text>
                </div>
                <div class="option-item" @click="toggleSelected(2)">
                    <image class="optionIcon" resize="cover"
                           :src="choice===2?chooseOption.selectIcon:chooseOption.icon"></image>
                    <text class="optionSpan">讲师</text>
                </div>
            </div>

            <!--<text class='category-title'>热门搜索</text>-->
            <!--<div class='tag-btns-container'>-->
                <!--<text v-for="(item,index) in hotKeys" :key="index" class='tag-btn' @click='onSearch'>{{item}}-->
                <!--</text>-->
            <!--</div>-->
            <!--<text class='category-title'>历史搜索</text>-->
            <!--<div class='tag-btns-container'>-->
                <!--<text v-for="(item,index) in historyKeys" :key="index" class='tag-btn' @click='onSearch'>{{item}}-->
                <!--</text>-->
            <!--</div>-->
        </div>
        <list v-else class="list">
            <cell v-if="searchListResults && searchListResults.length===0">
                <text class="noListResults">暂未搜索到相关课程！</text>
            </cell>
            <cell v-else v-for="(list,index) in searchListResults" :key="index" @click="onDetail(list)">
                <div class="search-cell">
                    <image class="search-image" resize="cover" :src="list.image"></image>
                    <div class="search-content">
                        <text class="search-describe">{{list.name}}</text>
                        <div class='search-gap'></div>
                        <text class='search-type'>{{CourseTypeString(list.type)}}</text>
                        <div class='search-gap'></div>
                        <text class='search-price'>{{list.price}}</text>
                    </div>
                </div>
            </cell>
        </list>
    </div>
</template>

<script>
import mixin from '@/modules/mixin'
import navbar from './components/nav-bar.vue'
import api from '@/modules/api'
var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {navbar},
  data () {
    return {
      hotKeys: ['前端开发', '王大锤老师'],
      historyKeys: ['移动开发', '前开发', '前端开发', '前发'],
      showCategory: true,
      searchListResults: null,
      choice: 1
    }
  },
  computed: {
    chooseOption () {
      return {
        icon: this.iconPath('icon_choice'),
        selectIcon: this.iconPath('icon_choice_o')
      }
    }
  },
  methods: {
    onDetail (item) {
      navigator.push({
        url: api.getUri('detail') + '?courseId=' + item.id,
        animated: true
      })
    },
    onSearch () {
    },
    searchContentInput (e) {
      this.searchValue = e.value
    },
    searchCourseOrTeacher () {
      this.searchListResults = null
      let url
      if (this.choice === 1) {
        url = '/edu/course/getCourseListByPage'
      } else if (this.choice === 2) {
        url = '/edu/course/getCoursePageByTeacherInfo'
      }
      this.showCategory = false
      let self = this
      api.get(url, {condition: this.searchValue, status: 2},
        function (data) {
          self.searchListResults = data.content.records
        },
        function () {

        })
    },
    toggleSelected (index) {
      this.choice = index
    },
    cancel () {
      this.showCategory = true
    }
  }
}
</script>

<style scoped>
    .cancel-btn {
        width: 140px;
        height: 50px;
        position: absolute;
        right: 20px;
        top: 25px;
        line-height: 50px;
        text-align: right;
        color: white;
    }

    .search-bg {
        height: 88px;
        background-color: #003797;
        flex-direction: row;
        justify-content: center;
        align-items: center;
    }

    .search-option {
        height: 88px;
        flex-direction: row;
        justify-content: center;
        align-items: center;
    }

    .option-item {
        width: 300px;
        height: 40px;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .optionIcon {
        width: 40px;
        height: 40px;
    }

    .optionSpan {
        font-size: 26px;
        color: #333;
        float: left;
        color: black;
        line-height: 40px;
        margin-left: 20px;
    }

    .search-bg-white {
        width: 710px;
        height: 68px;
        background-color: white;
        border-radius: 5px;
        flex-direction: row;
        justify-content: center;
        align-items: center;
    }

    .search-bg-input {
        height: 100%;
        width: 700px;
        font-size: 26px;
        text-align: center;
    }

    .category-title {
        margin-top: 30px;
        font-size: 28px;
        margin-left: 30px;
    }

    .tag-btns-container {
        margin-top: 10px;
        width: 750px;
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .search-image {
        width: 260px;
        height: 180px;
        margin-left: 20px;
        margin-top: 20px;
        border-radius: 10px;
    }

    .tag-btn {
        background-color: white;
        padding-left: 25px;
        padding-right: 25px;
        height: 60px;
        border-radius: 30px;
        border-color: lightgray;
        border-width: 1px;
        text-align: center;
        margin-bottom: 20px;
        margin-top: 20px;
        margin-left: 25px;
        font-size: 24px;
        line-height: 60px;
    }

    .search-cell {
        background-color: white;
        width: 750px;
        height: 220px;
        flex-direction: row;
        margin-bottom: 20px;
    }

    .search-content {
        margin-left: 20px;
        margin-right: 20px;
        margin-top: 20px;
        height: 180px;
    }

    .search-describe {
        font-size: 24px;
        color: #646464;
        word-break: break-all;
        word-wrap: break-word;
        width: 450px;
        max-height: 50px;
    }

    .noListResults {
        margin-top: 50px;
        text-align: center;
        color: #333;
        font-size: 32px;
    }

    .list {
        flex: 1;
        background-color: rgb(249, 249, 249);
    }

    .search-type {
        display: block;
        font-size: 20px;
        color: white;
        width: 60px;
        padding: 8px;
        text-align: center;
        border-radius: 5px;
        background-color: #0f3691;
    }

    .search-price {
        font-size: 28px;
        color: #ec5f59;
    }

    .search-gap {
        flex: 1;
    }

</style>
