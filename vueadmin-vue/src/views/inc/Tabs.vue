<template>
    <el-tabs v-model="editableTabsValue" type="card" closable @tab-remove="removeTab" @tab-click="clickTab">
        <el-tab-pane
                v-for="(item, index) in editableTabs"
                :key="item.name"
                :label="item.title"
                :name="item.name"
        >
        </el-tab-pane>
    </el-tabs>
</template>

<script>
    export default {
        name: "Tabs",
        computed: {
            // 导航列表
            editableTabsValue:{
                get(){
                    return this.$store.state.menus.editableTabsValue
                },
                set(val){
                    this.$store.state.menus.editableTabsValue = val
                }
            },
            // 导航数据
            editableTabs:{
                get(){
                    return this.$store.state.menus.editableTabs
                },
                set(val){
                    this.$store.state.menus.editableTabs = val
                }
            }
        },
        methods: {
            // 删除导航
            removeTab(targetName) {
                // 声明tabs变量将导航存放起来
                let tabs = this.editableTabs;
                // 声明activeName变量将导航数据存放起来
                let activeName = this.editableTabsValue;
                // if判断如果删除导航是首页，则不可删除
                if (targetName === 'Index') {
                    return
                }
                // if判断
                if (activeName === targetName) {
                    tabs.forEach((tab, index) => {
                        if (tab.name === targetName) {
                            let nextTab = tabs[index + 1] || tabs[index - 1];
                            if (nextTab) {
                                activeName = nextTab.name;
                            }
                        }
                    });
                }
                this.editableTabsValue = activeName;
                this.editableTabs = tabs.filter(tab => tab.name !== targetName);
                this.$router.push({name:activeName})
            },
            // 点击导航切换导航
            clickTab(target){
                // console.log(target)
                // router切换选中的导航
                this.$router.push({name:target.name})
            }
        }
    }
</script>

<style scoped>
</style>