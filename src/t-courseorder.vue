<template>
    <div>
        <nav-bar title="课程订单" show-back="true"></nav-bar>
        <div class='tag-view'>
            <div class='tag-btn' v-for="(item,index) in tablist" :key="index" @click="onTabClicked(index)">
                <text :class="['tag-text', tabIndex==index?'tag-text-selected':'']">{{item}}</text>
                <div :class="['tag-line', tabIndex==index?'tag-line-selected':'']"></div>
            </div>
        </div>

        <!--:style="{height:deviceHeight+'px'}"-->
        <slider class="slider" auto-play="false" :index="tabIndex" @change="onTabChanged">
            <order-list status="1" class="list"></order-list>
            <order-list status="2" class="list"></order-list>
        </slider>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import OrderList from './t-orderlist'

export default {
  components: {NavBar, OrderList},
  created () {
  },
  data () {
    return {
      tabIndex: 0,
      tablist: ['进行中', '已完成']
    }
  },
  methods: {
    onTabClicked (index) {
      this.tabIndex = index
    },
    onTabChanged (e) {
      this.tabIndex = e.index
    }
  }
}
</script>

<style scoped>
    .slider {
        flex: 1;
    }

    .list {
        background-color: rgb(249, 249, 249);
        flex: 1;
    }

    .tag-view {
        width: 750px;
        height: 80px;
        background-color: rgb(245, 245, 245);
        display: flex;
        flex-direction: row;
    }

    .tag-btn {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }

    .tag-text {
        margin-top: 20px;
        font-size: 28px;
    }

    .tag-text-selected {
        color: #0f3691;
    }

    .tag-line {
        margin-top: 20px;
        width: 80px;
        height: 5px;
        background-color: #003797;
        opacity: 0;
    }

    .tag-line-selected {
        opacity: 1;
    }
</style>
