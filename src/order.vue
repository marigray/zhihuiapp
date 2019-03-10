<template>
    <div class="wrapper">
        <navbar show-back="true" title="提交订单"></navbar>
        <scroller class="scroller" v-if="detailInfo">
            <div class="info-head">
                <div class="info-title">
                    <text class="info-teacher">{{teacherName}}</text>
                    <text class="info-time">上课时间：{{detailInfo.startDate}}-{{detailInfo.endDate}}</text>
                </div>
                <div class="cell">
                    <image class="image" resize="cover" :src="detailInfo.image"></image>
                    <div class="search-content">
                        <text class="describe">{{detailInfo.name}}</text>
                        <text class="tag">{{CourseTypeString(detailInfo.type)}}</text>
                        <text class='course-price'>{{detailInfo.price}}</text>
                    </div>
                </div>
            </div>
            <text class="pay-title" v-if="detailInfo.price && detailInfo.price != 0">请选择支付方式</text>
            <div class="payment-detail" v-if="detailInfo.price && detailInfo.price != 0" :style="{height:detailHeight}">
                <div class="detail">
                    <image class="detail-icon" :src="iconPath('order_wxzf')"></image>
                    <text class="unselect-txt">微信支付</text>
                    <image class="icon" :src="type===1?chooseOption.selectIcon:chooseOption.icon"
                           @click="choosePayType(1)"></image>
                </div>
                <div class="detail">
                    <image class="detail-icon" :src="iconPath('ordr_zfb')"></image>
                    <text class="unselect-txt">支付宝</text>
                    <image class="icon" :src="type===2?chooseOption.selectIcon:chooseOption.icon"
                           @click="choosePayType(2)"></image>
                </div>
                <div class="detail1">
                    <text class="unselect-txt1">其他支付方式</text>
                    <image class="icon" :src="type===3?chooseOption.selectIcon:chooseOption.icon"
                           @click="choosePayType(3)"></image>
                </div>
                <div class="detail1-content" v-if="showOtherPay">
                    <text class="pay-describe" style="font-weight: bold;align-items: left">银行转账</text>
                    <text class="pay-describe">{{payText1}}</text>
                    <text class="pay-describe" style="font-weight: bold;align-items: left">公司信用卡与国际信用卡支付</text>
                    <text class="pay-describe">{{payText2}}</text>
                    <text class="pay-describe" style="font-weight: bold;align-items: left">{{payText3}}</text>
                </div>
            </div>
            <!--<div class="detail2" v-if="detailInfo.price && detailInfo.price != 0" @click="onTitleSelect">-->
            <!--<text class="select-txt">开具发票</text>-->
            <!--<text class="input" :value="titleName"></text>-->
            <!--<image class="icon" :src="iconPath('icon_right')"></image>-->
            <!--</div>-->
            <div class="gap"></div>
            <div class="price-content">
                <text class="total-price">实付金额: {{detailInfo.price}}元</text>
                <div class="gap"></div>
                <text class="btn" @click="submit">提交订单</text>
            </div>
        </scroller>
    </div>
</template>

<script>
import navbar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import api from '@/modules/api'
import user from '@/modules/user'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {navbar},
  data () {
    return {
      type: 1,
      courseId: 182,
      detailInfo: null,
      teacherName: '',
      orderId: 0,
      titleId: 0,
      titleName: '开具发票',
      payText1:
        '杭州小同网络科技有限公司\n' +
        '招商银行股份有限公司杭州钱塘支行\n' +
        '571911205710701\n' +
        '\n',
      payText2: '请联系客服cs@xiaotongtech.net\n' +
        '\n',
      payText3: '支付完成后请发送支付凭证与订单号至客服邮箱 cs@xiaotongtech.net',
      showOtherPay: false,
      detailHeight: '270px'
    }
  },
  created () {
    this.getOrderInfo()
    var globalEvent = weex.requireModule('globalEvent');
    globalEvent.addEventListener('invoice-selected', function (e) {
      let result = e.result
      this.titleId = result.id
      this.titleName = decodeURIComponent(result.titleName)
    }.bind(this))
    // WeChatPay Callback result
    globalEvent.addEventListener('pay-result', function (e) {
      // let type = e.result.type
      // let cid = e.result.courseid
      let result = e.result.result
      if (result) {
        navigator.pop()
      }
    })
  },
  computed: {
    chooseOption: function chooseOption () {
      return {
        icon: this.iconPath('icon_choice'),
        selectIcon: this.iconPath('icon_choice_red')
      }
    }
  },
  methods: {
    choosePayType (type) {
      this.type = type
      if (type === 3) {
        this.showOtherPay = true
        this.detailHeight = '630px'
      } else {
        this.showOtherPay = false
        this.detailHeight = '270px'
      }
    },
    getOrderInfo () {
      let self = this
      api.get('/edu/course/getCourseInfoById', {id: this.courseId},
        function (data) {
          self.detailInfo = data.content.course
        },
        function (e) {
        })
    },
    onTitleSelect () {
      navigator.push({
        url: api.getUri('invoice1') + '?isInSelectionMode=true',
        animated: true
      })
    },
    submit () {
      if (this.detailInfo.price && this.detailInfo.price !== 0) {
        if (this.type === 1) {
          weex.requireModule('share').weChatPay2({
            body: this.detailInfo.name,
            totalFee: this.detailInfo.price * 100,
            cid: this.courseId,
            uid: user.userId(),
            billno: this.orderId,
            titleId: this.titleId
          })
        } else if (this.type === 2) {
          weex.requireModule('ali').alipay({
            body: this.detailInfo.name,
            subject: this.detailInfo.name,
            totalAmount: this.detailInfo.price,
            cid: this.courseId,
            uid: user.userId(),
            billno: this.orderId,
            titleId: this.titleId
          })
        } else if (this.type === 3) {
          let self = this
          api.post('/edu/order/OtherPayment', {cid: this.courseId, userid: user.userId(), orderid: this.orderId},
            function (data) {
              weex.requireModule('modal').toast({
                message: '订单提交成功',
                duration: 1000
              })
              navigator.pop()
            },
            function (e) {
              weex.requireModule('modal').toast({
                message: e.msg,
                duration: 1000
              })
            })
        }
      } else {

      }
    }
  }
}
</script>

<style scoped>
    .wrapper {
        background-color: rgb(240, 240, 240);
    }

    .scroller {
        padding: 20px;
    }

    .info {
        display: flex;
        flex-direction: column;
        height: 800px;
        background-color: white;
    }

    .info-head {
        background-color: #fff;
        width: 710px;
        height: 300px;
        border-radius: 5px;
        padding-left: 20px;
        padding-right: 20px;
        margin-top: 10px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
    }

    .info-teacher {
        font-size: 28px;
        color: #333333;
    }

    .info-time {
        margin-left: 20px;
        margin-right: 10px;
        color: #333333;
        font-size: 24px;
    }

    .info-title {
        width: 690px;
        height: 70px;
        border-bottom: 1px solid #eee;
        align-items: center;
        flex-direction: row;
    }

    .image {
        width: 260px;
        height: 180px;
        border-radius: 10px;
    }

    .search-content {
        margin-left: 20px;
    }

    .tag {
        background-color: rgb(15, 54, 145);
        color: #fff;
        border-radius: 5px;
        padding: 5px 0;
        width: 80px;
        text-align: center;
        margin-top: 20px;
        font-size: 24px;
    }

    .pay-title {
        background-color: #f3f2f4;
        color: #333;
        padding-left: 20px;
        height: 60px;
        line-height: 60px;
        margin-top: 40px;
    }

    .cell {
        background-color: white;
        flex-direction: row;
        margin-top: 10px;
    }

    .describe {
        white-space: normal;
        word-break: break-all;
        word-wrap: break-word;
        font-size: 24px;
        color: #333;
        max-width: 400px;
    }

    .payment-detail {
        width: 710px;
        height: 270px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        align-items: center;
    }

    .detail {
        width: 710px;
        height: 90px;
        padding-left: 20px;
        padding-right: 20px;
        background-color: white;
        border-bottom-width: 1px;
        border-bottom-style: solid;
        border-bottom-color: darkgray;
    }

    .detail1 {
        width: 710px;
        height: 90px;
        padding-left: 0;
        padding-right: 20px;
        background-color: white;
        border-bottom-width: 1px;
        border-bottom-style: solid;
        border-bottom-color: darkgray;
    }

    .detail1-content {
        padding-left: 20px;
        padding-right: 20px;
        padding-top: 20px;
    }

    .unselect-txt {
        font-size: 24px;
        color: #333333;
        left: 50px;
        top: 25px;
    }

    .unselect-txt1 {
        font-size: 24px;
        top: 25px;
        color: 333333;
        left: 20px;
        font-weight: bold;
    }

    .detail-icon {
        position: absolute;
        left: 20px;
        top: 20px;
        width: 40px;
        height: 40px;
    }

    .total-price {
        font-size: 28px;
        color: #ec5f59;
        margin-left: 40px;
    }

    .course-price {
        font-size: 28px;
        color: #ec5f59;
        margin-top: 30px;
    }

    .detail:not(:last-child) {
        border-bottom: 1px solid #eee;
    }

    .detail2 {
        width: 710px;
        height: 90px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        flex-direction: row;
        align-items: center;
        padding-left: 20px;
        padding-right: 20px;
        margin-top: 20px;
    }

    .icon {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 20px;
        right: 20px;
    }

    .input {
        font-size: 24px;
        height: 50px;
        flex: 1;
        line-height: 50px;
        margin-right: 50px;
        text-align: right;
        color: rgb(15, 54, 145);
    }

    .select-txt {
        font-size: 24px;
        color: #333333;
    }

    .price-content {
        flex-direction: row;
        bottom: 0;
        width: 710px;
        height: 100px;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        align-items: center;
    }

    .gap {
        flex: 1;
    }

    .btn {
        width: 150px;
        height: 80px;
        background-color: #e13529;
        color: #fff;
        border-radius: 5px;
        text-align: center;
        line-height: 80px;
    }

    .pay-describe {
        font-size: 24px;
        color: #333;
        padding-left: 20px;
        width: 710px;
        align-items: left;
    }
</style>
