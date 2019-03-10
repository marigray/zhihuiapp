<template>
    <div>
        <nav-bar show-back="true" title="服务条款"></nav-bar>
        <div class="frame">
            <div class='top'>
                <scroller class="scroller" show-scrollbar="false">
                    <div class="title">
                        <text style="font-size:28px;color:#0f3691">{{title}}</text>
                    </div>
                    <div class="content">
                        <text style="font-size:24px;color:#646464">{{eula}}</text>
                    </div>
                </scroller>
            </div>
            <!--<div class='bottom'>-->
                <!--<text class="cancel">取消</text>-->
                <!--<text class="confirm">同意并继续</text>-->
            <!--</div>-->
        </div>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'

export default {
  components: {NavBar},
  data () {
    return {
      title: '欢迎并感谢您使用智荟所提供的各项服务',
      eula: '        最终用户许可协议（End User Licence Agreement，EULA），指的是一家公司的软件与软件的使用者所达成的协议，此协议一般出现在软件安装时。如果使用者拒绝接受这家公司的EULA，那么便不能安装此软件。最终用户许可协议是软件应用程序作者或者发布者与应用程序使用者之间的合法合同。最终用户许可协议（EULA），通常是指“软件许可”，它与租赁协议类似；用户同意支付软件的使用费用，并且向软件作者或者发行者承诺遵守EULA中规定的所有约束条件。用户被告知，当他们打开软件包的包装、打开CD盒的封条、将卡片寄回给软件发行者、安装应用程序、执行下载文件或者简单的使用应用程序的时候就意味着他们已经“接受”了EULA中的条款。用户可以寄回软件产品或者在安装过程中当EULA提示接受按钮的时候点击“我不接受”来拒绝这个协议。 \n\n        一般情况下EULA最少有2000字，而且软件安装的时候，显示EULA的方框并不是很大，所以绝大多数人都是直接接受EULA的。\n\n        EULA主要规定用户不可以盗版软件。'
    }
  }
}
</script>

<style scoped>
    .frame {
        flex: 1;
        flex-direction: column;
        justify-content: space-between;
        align-items: center;
    }

    .top {
        flex: 1;
        flex-direction: column;
        justify-content: space-between;
        align-items: center;
        box-shadow: 0px 0px 20px 0px #576376;
    }

    .scroller {
        flex: 1;
        margin-top: 20px;
        margin-bottom: 20px;
        background-color: rgb(245, 245, 245);
    }

    .title {
        width: 710px;
        margin-top: 30px;
        margin-bottom: 30px;
        justify-content: center;
        align-items: center;
    }

    .content {
        width: 710px;
        padding-left: 20px;
        padding-right: 20px;
    }

    .bottom {
        width: 750px;
        height: 120px;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }

    .cancel {
        width: 160px;
        height: 70px;
        margin-left: 30px;
        border-radius: 5px;
        background-color: #c5c0cb;
        justify-content: center;
        align-items: center;
        font-size: 30px;
        color: #ffffff;
        text-align: center;
        line-height: 70px;
    }

    .confirm {
        width: 500px;
        height: 70px;
        margin-right: 30px;
        border-radius: 5px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 30px;
        color: #ffffff;
        text-align: center;
        line-height: 70px;
    }

    .text {
        font-size: 30px;
        color: #ffffff;
    }
</style>
