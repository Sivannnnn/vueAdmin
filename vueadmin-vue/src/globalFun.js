import vue from 'vue';

vue.mixin({
    methods: {
        hasAuth(perm) {
            // 获取menus的用户权限信息permList并存入authority变量
            var authority = this.$store.state.menus.permList
            console.log(authority)
            // authority.indexOf(perm)判断perm是否存在于authority中，若存在，则返回首次出现的位置，不存在，返回-1
            // 将返回结果与 -1 做判断，如果大于 -1，最后返回true，否则返回fasle
            return authority.indexOf(perm) > -1
        }
    }
})