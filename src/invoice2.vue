<template>
    <div>
        <nav-bar show-back="true" title="开具发票"></nav-bar>
        <scroller>
            <!--<div class="cell">-->
                <!--<text style="font-size: 28px;color: #333333;margin-bottom: 40px">发票类型</text>-->

                <!--<div class="subrow">-->
                    <!--<div v-for="(item,index) in invoiceTab" :key="index" @click="onInvoiceTabClicked(index)">-->
                        <!--<div :class="[invoiceIndex==index?'select':'unselect']">-->
                            <!--<text :class="[invoiceIndex==index?'select-txt':'unselect-txt']">{{item}}</text>-->
                        <!--</div>-->
                    <!--</div>-->
                <!--</div>-->

            <!--</div>-->
            <div class="invoice-info" v-if="invoiceIndex==0">
                <div class="midline">
                    <text class="midline-text">请填写发票信息
                    </text>
                </div>
                <div class='radio-view'>
                    <div class='radio-btn' v-for="(item,index) in tablist" :key="index" @click="onTabClicked(index)">
                        <div :class="['radio-circle', tabIndex==index?'radio-circle-selected':'']">
                            <image class="radio-circle-image"
                                   :src="tabIndex==index?iconPath('icon_choice_o'):iconPath('icon_choice')"></image>
                        </div>
                        <text :class="['radio-text', tabIndex==index?'radio-text-selected':'']">{{item}}</text>
                    </div>
                </div>
                <div class='info' v-if="tabIndex==0">
                    <div class="detail">
                        <text class="unselect-txt">发票抬头</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputTitle" :value="title"></input>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">地址</text>
                        <text style="font-size:24px;color:#646464">（设为默认地址）</text>
                        <input class="input" @input="inputAddress" :value="address"></input>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">联系电话</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputPhone" type="number" :value="phone"></input>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">邮寄地址</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputEmail" :value="email"></input>
                    </div>
                </div>
                <div class='info' v-if="tabIndex==1">
                    <div class="detail">
                        <text class="unselect-txt">发票抬头</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputTitle" :value="title"></input>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">纳税人识别号</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputDutyParagraph" :value="dutyParagraph"></input>
                    </div>
                    <text class="select-txt" style="margin-top:40px;margin-bottom:20px;">增值税专用发票请填写以下内容</text>
                    <div class="detail">
                        <text class="unselect-txt">联系电话</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputPhone" type="number" :value="phone"></input>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">邮寄地址</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputEmail" :value="email"></input>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">单位地址</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputAddress" :value="address"></input>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">开户银行</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputBankName" :value="bankName"></input>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">银行帐号</text>
                        <text style="font-size:24px;color:#646464">（必填）</text>
                        <input class="input" @input="inputBankAccount" type="number" :value="bankAccount"></input>
                    </div>
                </div>
            </div>
            <div class="btn" @click="onBtnClicked">
                <text style="font-size: 28px;color: #ffffff;">确定</text>
            </div>
        </scroller>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import user from '@/modules/user'
import api from '@/modules/api'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      invoiceIndex: 0,
      tabIndex: 0,
      invoiceTab: ['开具发票', '不开发票'],
      tablist: ['个人', '公司'],
      id: 0,
      type: 'add',
      title: '',
      address: '',
      phone: '',
      email: '',
      bankName: '',
      dutyParagraph: '', // 纳税人识别号
      bankAccount: ''
    }
  },
  created () {
    if (this.type !== 'add') {
      this.getInvoice()
    }
  },
  methods: {
    onInvoiceTabClicked (index) {
      this.invoiceIndex = index
    },
    onTabClicked (index) {
      this.tabIndex = index
    },
    onNavigatorPop () {
      navigator.pop()
    },
    inputTitle (e) {
      this.title = e.value
    },
    inputAddress (e) {
      this.address = e.value
    },
    inputPhone (e) {
      this.phone = e.value
    },
    inputEmail (e) {
      this.email = e.value
    },
    inputDutyParagraph (e) {
      this.dutyParagraph = e.value
    },
    inputBankName (e) {
      this.bankName = e.value
    },
    inputBankAccount (e) {
      this.bankAccount = e.value
    },
    onBtnClicked () {
      if (this.tabIndex === 0) {
        if (this.title.length === 0 || this.address.length === 0 || this.phone.length === 0 || this.email.length === 0) {
          weex.requireModule('modal').toast({
            message: '请完善发票信息',
            duration: 1000
          })
          return
        }
      } else if (this.tabIndex === 1) {
        if (!this.title.length === 0 || this.address.length === 0 || this.phone.length === 0 ||
          this.email.length === 0 || this.bankName.length === 0 || this.dutyParagraph.length === 0 ||
          this.bankAccount.length === 0) {
          weex.requireModule('modal').toast({
            message: '请完善发票信息',
            duration: 1000
          })
          return
        }
      }
      if (this.tabIndex === 0) {
        this.submit({
          uid: user.userId(),
          title: this.title,
          address: this.address,
          phone: this.phone,
          mailingAddress: this.email,
          type: 1
        })
      } else {
        this.submit({
          uid: user.userId(),
          title: this.title,
          dutyParagraph: this.dutyParagraph,
          phone: this.phone,
          mailingAddress: this.email,
          address:this.address,
          type: 2,
          bank: this.bankName,
          bankAccount: this.bankAccount
        })
      }
    },
    submit (parameters) {
      if (this.type === 'add') {
        api.post('/edu/invoice/addInvoiceTitle', parameters,
          function (data) {
            weex.requireModule('modal').toast({
              message: '添加成功',
              duration: 1000
            })
            navigator.pop()
            weex.requireModule('myevent').sendGlobal('invoice-add', {})
            navigator.pop()
          },
          function () {

          })
      } else if (this.type === 'edit') {
        parameters.id = this.id
        api.post('/edu/invoice/updateInvoiceTitle', parameters,
          function (data) {
            weex.requireModule('modal').toast({
              message: '修改成功',
              duration: 1000
            })
            weex.requireModule('myevent').sendGlobal('invoice-add', {})
            navigator.pop()
          },
          function () {

          })
      }
    },
    getInvoice () {
      let self = this
      api.get('/edu/invoice/getInvoiceTitleInfo', {id: this.id},
        function (data) {
          let invoice = data.content
          self.tabIndex = invoice.type - 1
          self.title = invoice.title
          self.address = invoice.address
          self.phone = invoice.phone
          self.email = invoice.mailingAddress
          self.dutyParagraph = invoice.dutyParagraph
          self.bankAccount = invoice.bankAccount
          self.bankName = invoice.bank
        },
        function (e) {
        })
    }
  }
}
</script>

<style scoped>
    .cell {
        width: 700px;
        height: 250px;
        margin-top: 20px;
        margin-left: 25px;
        padding-left: 40px;
        padding-right: 40px;
        margin-bottom: 20px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        align-items: flex-start;
        justify-content: flex-end;
        background-color: rgb(255, 255, 255);
    }

    .subrow {
        width: 630px;
        margin-bottom: 60px;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }

    .select {
        width: 300px;
        height: 80px;
        border-width: 2px;
        border-style: solid;
        border-color: rgb(25, 53, 140);
        border-radius: 5px;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #0f3691;
    }

    .select-txt {
        font-size: 24px;
        color: #0f3691;
    }

    .unselect {
        width: 300px;
        height: 80px;
        border-width: 2px;
        border-style: solid;
        border-color: #8c8295;
        border-radius: 5px;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #333333;
    }

    .unselect-txt {
        font-size: 24px;
        color: #333333;
    }

    .invoice-info {
        display: flex;
        flex-direction: column;
    }

    .midline {
        width: 750px;
        height: 80px;
        background-color: #f3f2f4;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .midline-text {
        font-size: 24px;
        margin-left: 25px;
        height: 80px;
        line-height: 80px;
    }

    .radio-view {
        width: 750px;
        height: 100px;
        padding-left: 80px;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .radio-btn {
        width: 180px;
        height: 100px;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .radio-text {
        margin-left: 10px;
        font-size: 24px;
    }

    .radio-text-selected {
        color: #0f3691;
    }

    .radio-circle {
        width: 30px;
        height: 30px;
        border-radius: 15px;
        border-width: 2px;
        border-color: #8c8295;
        background-color: #ffffff;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .radio-circle-selected {
    }

    .radio-circle-image {
        width: 30px;
        height: 30px;
    }

    .info {
        display: flex;
        flex-direction: column;
        padding-left: 40px;
        padding-right: 40px;
    }

    .detail {
        width: 670px;
        height: 90px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        /* opacity: 0.5; */
        flex-direction: row;
        align-items: center;
        padding-left: 20px;
        padding-right: 20px;
        margin-top: 20px;
    }

    .input {
        font-size: 24px;
        color: #333333;
        flex: 1;
        height: 40px;
        text-align: right;
    }

    .btn {
        margin-top: 70px;
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
</style>
