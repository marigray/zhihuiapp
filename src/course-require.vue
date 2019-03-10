<template>
    <div>
        <nav-bar show-back="true" title="发布课程需求"></nav-bar>
        <div class="info">
            <text class="course-title">请描述您的课程需求</text>
            <div class="detail">
                <text class="select-txt">所属行业</text>
                <text class="input" @click="showIndustryPop">{{selectedIndustryName}}</text>
                <image class="icon" :src="iconPath('icon_down')"></image>
            </div>
            <div class="detail">
                <text class="select-txt">适用职能</text>
                <text class="input" @click="showFunctionPop">{{selectedFunctionName}}</text>
                <image class="icon" :src="iconPath('icon_down')"></image>
            </div>
            <div class="detail1">
                <text class="select-txt">请具体描述一下您的需求</text>
                <textarea class="textarea" @input="requireInput"></textarea>
            </div>
        </div>
        <div class="btn" @click="publishCourse">
            <text style="font-size: 28px;color: #ffffff;">完成</text>
        </div>

        <wxc-popup :show="isIndustryPopShow" @wxcPopupOverlayClicked="hidePop" pos="bottom" height="500">
            <scroller class="pop-scroller">
                <wxc-cell :has-top-border="false" :auto-accessible="false" v-for="item in industryList" :key="item.id"
                          :title="item.name" @wxcCellClicked="setCurrentIndustry(item)">
                    <image class="pop-selected" v-if="item.id===selectedIndustryId" :src="iconPath('icon_sus')"></image>
                </wxc-cell>
            </scroller>
        </wxc-popup>
        <wxc-popup :show="isFunctionPopShow" @wxcPopupOverlayClicked="hidePop" pos="bottom" :height="500">
            <scroller class="pop-scroller">
                <wxc-cell :has-top-border="false" :auto-accessible="false" v-for="item in functionList" :key="item.id"
                          :title="item.name" @wxcCellClicked="setCurrentFunction(item)">
                    <image v-if="item.id===selectedFunctionId" :src="iconPath('icon_sus')"></image>
                </wxc-cell>
            </scroller>
        </wxc-popup>
    </div>

</template>

<script>
import NavBar from './components/nav-bar.vue'
import {WxcButton, WxcCell, WxcPopup} from 'weex-ui'
import mixin from '@/modules/mixin'
import api from '@/modules/api'
import user from '@/modules/user'

export default {
  mixins: [mixin],
  components: {NavBar, WxcCell, WxcPopup, WxcButton},
  data () {
    return {
      industryList: [],
      functionList: [],
      selectedIndustryName: '',
      selectedFunctionName: '',
      isIndustryPopShow: false,
      isFunctionPopShow: false,
      selectedIndustryId: 0,
      selectedFunctionId: 0,
      require: null
    }
  },
  created () {
    this.getIndustryList()
    this.getFunctionList()
  },
  methods: {
    hidePop () {
      this.isIndustryPopShow = false
      this.isFunctionPopShow = false
    },
    showFunctionPop () {
      this.isFunctionPopShow = true
    },
    setCurrentIndustry (item) {
      this.selectedIndustryId = item.id
      this.selectedIndustryName = item.name
      this.isIndustryPopShow = false
    },
    setCurrentFunction (item) {
      this.selectedFunctionId = item.id
      this.selectedFunctionName = item.name
      this.isFunctionPopShow = false
    },
    showIndustryPop () {
      this.isIndustryPopShow = true
    },
    getIndustryList () {
      let self = this
      api.get('/edu/dic/getIndustryList', null,
        function (data) {
          self.industryList = data.content
        },
        function () {
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
    publishCourse () {
      let self = this
      api.post('/edu/user/addCourseRequirement',
        {
          uid: user.userId(),
          industry: self.selectedIndustryName,
          function: self.selectedFunctionName,
          require: self.require
        },
        function (data) {
          console.log(data);
          weex.requireModule('modal').toast({
            message: '发布成功',
            duration: 1000
          })
          navigator.pop()
        },
        function () {
        })
    },
    requireInput (e) {
      this.require = e.value
    }
  }
}
</script>

<style scoped>
    .info {
        display: flex;
        flex-direction: column;
        padding-left: 20px;
        padding-right: 20px;
    }

    .course-title {
        font-size: 30px;
        color: #333;
        margin-top: 20px;
    }

    .detail {
        width: 710px;
        height: 90px;
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

    .detail1 {
        width: 710px;
        height: 180px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        flex-direction: column;
        padding-left: 20px;
        padding-right: 20px;
        padding-top: 20px;
        margin-top: 20px;
    }

    .icon {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 20px;
        right: 20px;
    }

    .select-txt {
        font-size: 24px;
        color: #333333;
    }

    .textarea {
        font-size: 24px;
        color: #333333;
        flex: 1;
        margin: 20px;
    }

    .btn {
        margin-top: 50px;
        margin-bottom: 20px;
        margin-left: 30px;
        width: 690px;
        height: 90px;
        border-radius: 5px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 28px;
        color: #ffffff;
    }

    .input {
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
