<template>
    <div>
        <nav-bar show-back="true" :title="navTitle" showSearch="true"></nav-bar>
        <div class='tab-view'>
            <div class='tab-btn' v-for="(item,index) in tablist" :key="index" @click="onTabClicked(index)">
                <text :class="['tab-text', tabIndex==index?'tab-text-selected':'']">{{item}}</text>
                <div :class="['tab-line', tabIndex==index?'tab-line-selected':'']"></div>
            </div>
        </div>
        <slider class="slider" auto-play="false" :index="tabIndex" @change="onTabChanged">
            <course-category-list :industryId="industryId"
                                  :functionId="functionId" type="0" class="list"></course-category-list>
            <course-category-list :industryId="industryId"
                                  :functionId="functionId" type="1" class="list"></course-category-list>
            <course-category-list :industryId="industryId"
                                  :functionId="functionId" type="2" class="list"></course-category-list>
            <course-category-list :industryId="industryId"
                                  :functionId="functionId" type="3" class="list"></course-category-list>
        </slider>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import CellCategoryHeader from './components/cell-category-header.vue'
import CourseCategoryList from './course-category-list.vue'
import mixin from '@/modules/mixin'

export default {
  mixins: [mixin],
  components: {NavBar, CellCategoryHeader, CourseCategoryList},
  data () {
    return {
      navTitle: null,
      industryId: 0,
      functionId: 0,
      tabIndex: 0,
      tablist: ['全部', '线上', '线下', '专栏']
    }
  },
  computed: {
  },
  methods: {
    onTabClicked (i) {
      this.tabIndex = i
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

    .tab-view {
        width: 750px;
        height: 80px;
        background-color: rgb(245, 245, 245);
        display: flex;
        flex-direction: row;
    }

    .tab-btn {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }

    .tab-text {
        margin-top: 20px;
        font-size: 28px;
    }

    .tab-text-selected {
        color: #0f3691;
    }

    .tab-line {
        margin-top: 20px;
        width: 80px;
        height: 5px;
        background-color: #003797;
        opacity: 0;
    }

    .tab-line-selected {
        opacity: 1;
    }

    .list {
        flex: 1;
    }

</style>
