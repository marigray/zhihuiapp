<template>
    <div class='cell-category-header-container'>
        <div class='cell-category-header-line'></div>
        <text class='cell-category-header-title'>{{title}}</text>
    </div>
</template>

<script>
export default {
  name: 'cell-category-header',
  props: ['title', 'marginLeft']
}
</script>

<style scoped>
    .cell-category-header-container {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .cell-category-header-line {
        width: 10px;
        height: 40px;
        background-color: rgb(0, 54, 142);
    }

    .cell-category-header-title {
        display: inline-block;
        font-size: 32px;
        color: #0f3691;
        height: 80px;
        line-height: 80px;
        margin-left: 20px;
    }
</style>
