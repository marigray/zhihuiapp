<template>
    <div>
        <navbar title="分类" showSearch="true">
            <image slot="right-icon1" class="search" :src="iconPath('header_search')" @click="onSearch"></image>
        </navbar>
        <div>
            <scroller class='left-menu' :style="{height:deviceHeight}">
                <div v-for="(item,index) in industryList" :key="index" @click="tabClicked(index)"
                     :class="['category-tab', categoryIndex==index?'category-tab-selected':'']">
                    <text :class="['category-title', categoryIndex==index?'category-title-selected':'']">{{item.name}}
                    </text>
                </div>
            </scroller>
            <scroller class='right-menu' scroll-y="true" :style="{height:deviceHeight}">
                <image class='category-image' mode="aspectFill"
                       src="http://img02.tooopen.com/images/20150928/tooopen_sy_143912755726.jpg"
                       @click="onTagClicked()"></image>
                <div>
                    <text class="right-category-title">{{currentCategoryTitle}}</text>
                </div>
                <div class='tag-btns-container'>
                    <text v-for="(item, index) in currentFunctionList" :key="index" class="tag-btn"
                          @click="onTagClicked(item)">{{item.name}}
                    </text>
                </div>
            </scroller>
        </div>
    </div>
</template>

<script>
import navbar from './components/nav-bar.vue'
import api from '@/modules/api'
import mixin from '@/modules/mixin'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {navbar},
  data () {
    return {
      categoryIndex: 0,
      functionList: [],
      industryList: []
    }
  },
  computed: {
    currentCategoryTitle () {
      if (this.industryList.length <= this.categoryIndex) return ''
      let item = this.industryList[this.categoryIndex]
      return item.name
    },

    currentFunctionList () {
      if (this.industryList.length <= this.categoryIndex) return null
      let item = this.industryList[this.categoryIndex]
      let array = []
      for (let i = 0; i < this.functionList.length; i++) {
        let subItem = this.functionList[i]
        if (subItem.industryid === item.id) {
          array.push(subItem)
        }
      }
      return array
    }
  },
  methods: {
    onSearch () {
      navigator.push({
        url: api.getUri('search'),
        animated: true
      })
    },
    tabClicked (index) {
      this.categoryIndex = index
      this.$forceUpdate()
    },
    onTagClicked (item) {
      let categoryItem = this.industryList[this.categoryIndex]
      if (categoryItem.id === 36) {
        navigator.push({
          url: api.getUri('recommend-list'),
          animated: true
        })
        return
      }
      navigator.push({
        url: api.getUri('course-category') + '?industryId=' + categoryItem.id + '&functionId=' + item.id + '&navTitle=' + item.name,
        animated: true
      })
    },
    getFunctionList () {
      let self = this
      api.get('/edu/dic/getFunctionList', null,
        function (data) {
          self.functionList = data.content
        },
        function () {

        })
    },
    getIndustryList () {
      let self = this
      api.get('/edu/dic/getIndustryList', null,
        function (data) {
          self.industryList = data.content
        },
        function () {

        })
    }
  },
  created () {
    this.getIndustryList()
    this.getFunctionList()
  }
}
</script>

<style scoped>
    .nav-bar-gap {
        height: 148px;
    }

    .search {
        width: 40px;
        height: 40px;
        position: absolute;
        right: 20px;
        top: 30px;
    }

    .category-section {
        border: 1px solid #eee;
        margin-bottom: 20px;
        box-shadow: 0px 0px 10px 5px #f0f0f0;
        flex-direction: row;
        align-items: center;
        margin-left: 0px;
        margin-right: 0px;
        margin-top: 20px;
        background-color: white;
    }

    .left-icon {
        margin-top: 72px;
        margin-left: 20px;
        width: 44px;
        height: 44px;
    }

    .left-menu {
        width: 150px;
        background-color: rgb(245, 245, 245);
        position: absolute;
    }

    .right-menu {
        margin-left: 150px;
        width: 600px;
        background-color: white;
    }

    .category-image {
        margin: 30px;
        width: 540px;
        height: 180px;
    }

    /* // 0 54 142 */
    .category-tab {
        width: 150px;
        height: 80px;
        text-align: center;
        line-height: 80px;
        margin-top: 5px;
        margin-bottom: 0px;
        font-size: 24px;
        justify-content: center;
        align-items: center;
    }

    .category-tab-selected {
        color: white;
        background-color: rgb(0, 54, 142);
    }

    .category-title {
        text-align: center;
        font-size: 24px;
        height: 80px;
        line-height: 80px;
        color: #333333;
    }

    .category-title-selected {
        color: white;
    }

    .right-category-title {
        margin-left: 30px;
        font-size: 28px;
        color: #333333;
    }

    .tag-btns-container {
        margin-top: 10px;
        width: 600px;
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .tag-btn {
        width: 168px;
        height: 60px;
        border-width: 1px;
        border-style: solid;
        border-color: lightgray;
        border-radius: 30px;
        text-align: center;
        margin-bottom: 20px;
        margin-top: 20px;
        margin-left: 25px;
        font-size: 24px;
        line-height: 60px;
        color: #333333;
    }
</style>
