<template>
    <div>
        <el-form :inline="true">
            <el-form-item>
                <el-input
                        v-model="searchForm.username"
                        placeholder="用户名"
                        clearable
                >
                </el-input>
            </el-form-item>

            <el-form-item>
                <el-button @click="getUserList">搜索</el-button>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="dialogVisible = true" v-if="hasAuth('sys:user:save')">新增</el-button>
            </el-form-item>
            <el-form-item>
                <el-popconfirm title="这是确定批量删除吗？" @confirm="delHandle(null)">
                    <el-button type="danger" slot="reference" :disabled="delBtlStatu" v-if="hasAuth('sys:user:delete')">批量删除</el-button>
                </el-popconfirm>
            </el-form-item>
        </el-form>

        <el-table
                ref="multipleTable"
                :data="tableData"
                tooltip-effect="dark"
                style="width: 100%"
                border
                stripe
                @selection-change="handleSelectionChange">
            <el-table-column
                    type="selection"
                    width="55">
            </el-table-column>
            <el-table-column
                    label="头像"
                    width="50">
                <template slot-scope="scope">
                    <el-avatar size="small" :src="scope.row.avatar"></el-avatar>
                </template>
            </el-table-column>
            <el-table-column
                    prop="username"
                    label="用户名"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="code"
                    label="角色名称">
                <template slot-scope="scope">
                    <el-tag size="small" type="info" v-for="item in scope.row.sysRoles">{{item.name}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column
                    prop="email"
                    label="邮箱">
            </el-table-column>
            <el-table-column
                    prop="phone"
                    label="手机号">
            </el-table-column>
            <el-table-column
                    prop="statu"
                    label="状态">
                <template slot-scope="scope">
                    <el-tag size="small" v-if="scope.row.statu === 1" type="success">正常</el-tag>
                    <el-tag size="small" v-else-if="scope.row.statu === 0" type="danger">禁用</el-tag>
                </template>
            </el-table-column>
            <el-table-column
                    prop="created"
                    width="200"
                    label="创建时间"
            >
            </el-table-column>
            <el-table-column
                    prop="icon"
                    width="260px"
                    label="操作">

                <template slot-scope="scope">
                    <el-button type="text" @click="roleHandle(scope.row.id)">分配角色</el-button>
                    <el-divider direction="vertical"></el-divider>

                    <el-button type="text" @click="repassHandle(scope.row.id, scope.row.username)">重置密码</el-button>
                    <el-divider direction="vertical"></el-divider>

                    <el-button type="text" @click="editHandle(scope.row.id)">编辑</el-button>
                    <el-divider direction="vertical"></el-divider>

                    <template>
                        <el-popconfirm title="这是一段内容确定删除吗？" @confirm="delHandle(scope.row.id)">
                            <el-button type="text" slot="reference">删除</el-button>
                        </el-popconfirm>
                    </template>

                </template>
            </el-table-column>
        </el-table>

        <!-- 分页页数-->
        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                layout="total, sizes, prev, pager, next, jumper"
                :page-sizes="[10, 20, 50, 100]"
                :current-page="current"
                :page-size="size"
                :total="total">
        </el-pagination>

        <!--新增表单对话框-->
        <el-dialog
                title="提示"
                :visible.sync="dialogVisible"
                width="600px"
                :before-close="handleClose">

            <el-form :model="editForm" :rules="editFormRules" ref="editForm">
                <el-form-item label="用户名" prop="username" label-width="100px">
                    <el-input v-model="editForm.username" autocomplete="off"></el-input>
                    <el-alert
                            title="初始密码为888888"
                            :closable="false"
                            type="info"
                            style="line-height: 12px;"
                    ></el-alert>
                </el-form-item>

                <el-form-item label="邮箱"  prop="email" label-width="100px">
                    <el-input v-model="editForm.email" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="手机号"  prop="phone" label-width="100px">
                    <el-input v-model="editForm.phone" autocomplete="off"></el-input>
                </el-form-item>

                <el-form-item label="状态"  prop="statu" label-width="100px">
                    <el-radio-group v-model="editForm.statu">
                        <el-radio :label="0">禁用</el-radio>
                        <el-radio :label="1">正常</el-radio>
                    </el-radio-group>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="resetForm('editForm')">取 消</el-button>
                <el-button type="primary" @click="submitForm('editForm')">确 定</el-button>
            </div>
        </el-dialog>

        <!-- 分配权限表单对话框 -->
        <el-dialog title="分配角色" :visible.sync="roleDialogFormVisible" width="600px">
            <el-form :model="roleForm">
                <el-tree
                        :data="roleTreeData"
                        show-checkbox
                        ref="roleTree"
                        :check-strictly=checkStrictly
                        node-key="id"
                        :default-expand-all=true
                        :props="defaultProps">
                </el-tree>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="roleDialogFormVisible=false">取 消</el-button>
                <el-button type="primary" @click="submitRoleHandle('roleForm')">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "Role",
        data() {
            return {
                searchForm: {},     // 搜索框
                delBtlStatu: true,  // 批量删除disabled激活状态
                total: 0,       // 数据总数
                size: 10,       // 查看列表数量
                current: 1,     //当前页数
                dialogVisible: false,   // 删除对话框
                editForm: {},   // 编辑表单数据
                tableData: [],  // 用户列表数据
                // 编辑表单校对规则
                editFormRules: {
                    username: [
                        {required: true, message: '请输入用户名称', trigger: 'blur'}
                    ],
                    email: [
                        {required: true, message: '请输入邮箱', trigger: 'blur'}
                    ],
                    statu: [
                        {required: true, message: '请选择状态', trigger: 'blur'}
                    ]
                },

                multipleSelection: [],  // 多选数组
                roleDialogFormVisible: false,   // 分配角色对话框
                // 标签节点
                defaultProps: {
                    children: 'children',
                    label: 'name'
                },
                roleForm: {},   // 角色表单
                roleTreeData:  [],      // 角色数据
                treeCheckedKeys: [],    // 数据选中索引
                checkStrictly: true     // 严格选中
            }
        },
        created() {
            // 获取用户列表
            this.getUserList()
            // 发送get请求获取用户列表
            this.$axios.get("/sys/role/list").then(res => {
                // 请求成功将用户信息赋给声明的用户列表数组对应的变量
                this.roleTreeData = res.data.data.records
            })
        },
        methods: {
            // 角色列表多选操作
            handleSelectionChange(val) {
                console.log("勾选")
                console.log(val)
                // 筛选后将该id存在多选数组
                this.multipleSelection = val;
                // 如果列表有被选中，通过多选数组的长度来判断是否 批量删除 激活效果
                this.delBtlStatu = val.length == 0
            },
            // 修改列表展示数据量
            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
                this.size = val
                this.getUserList()
            },
            // 切换当前的页数
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
                this.current = val
                this.getUserList()
            },
            // 重置
            resetForm(formName) {
                this.$refs[formName].resetFields();
                this.dialogVisible = false
                this.editForm = {}
            },
            // 关闭对话框
            handleClose() {
                this.resetForm('editForm')
            },
            // 获取用户数据
            getUserList() {
                // 发送get请求获取用户数据
                this.$axios.get("/sys/user/list", {
                    // 携带搜索框内容，当前页数，和页码大小
                    params: {
                        username: this.searchForm.username,
                        current: this.current,
                        size: this.size
                    }
                }).then(res => {
                    this.tableData = res.data.data.records
                    this.size = res.data.data.size
                    this.current = res.data.data.current
                    this.total = res.data.data.total
                })
            },
            // 提交表单
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        // 为了减少代码里，实现路径判断公用
                        // 通过 this.editForm.id 是否存在判断 如果存在，那就是更新（update） 否则为创建保存（save）
                        this.$axios.post('/sys/user/' + (this.editForm.id?'update' : 'save'), this.editForm)
                            .then(res => {
                                // 请求成功，消息弹出成功提示
                                this.$message({
                                    showClose: true,
                                    message: '恭喜你，操作成功',
                                    type: 'success',
                                    onClose:() => {
                                        this.getUserList()
                                    }
                                });
                                // 关闭对话框
                                this.dialogVisible = false
                            })
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
            // 点击编辑打开编辑对话框
            editHandle(id) {
                // 发送get请求获取当前角色的数据信息
                this.$axios.get('/sys/user/info/' + id).then(res => {
                    // 请求成功，将服务器返回的结果赋值给编辑列表
                    this.editForm = res.data.data
                    // 打开编辑对话框
                    this.dialogVisible = true
                })
            },
            // 删除（批量删除和单个删除）
            delHandle(id) {
                // 声明ids数组
                var ids = []
                // if判断触发方法传来的参数是否存在
                if (id) {
                    // 如果id存在，将id存入ids数组
                    ids.push(id)
                } else {
                    // 否则为不存在，就是批量删除，将用户列表所有的id存入ids数组
                    this.multipleSelection.forEach(row => {
                        ids.push(row.id)
                    })
                }
                // console.log(ids)
                // 发送post请求 携带ids数组
                this.$axios.post("/sys/user/delete", ids).then(res => {
                    // 请求成功，消息弹出成功提示
                    this.$message({
                        showClose: true,
                        message: '恭喜你，操作成功',
                        type: 'success',
                        onClose:() => {
                            // 重新获取角色数据
                            this.getUserList()
                        }
                    });
                })
            },
            // 分配权限
            roleHandle (id) {
                // 打开对话框
                this.roleDialogFormVisible = true
                // 发送get请求获取该id的信息
                this.$axios.get('/sys/user/info/' + id).then(res => {
                    // 请求成功将返回值信息赋给对话框
                    this.roleForm = res.data.data
                    // 声明一个用户id的空数组
                    let roleIds = []
                    // 遍历返回结果数组将结果赋给用户id的空数组
                    res.data.data.sysRoles.forEach(row => {
                        roleIds.push(row.id)
                    })
                    this.$refs.roleTree.setCheckedKeys(roleIds)
                })
            },
            // 提交分配权限表单
            submitRoleHandle(formName) {
                var roleIds = this.$refs.roleTree.getCheckedKeys()
                console.log(roleIds)
                // 发送post请求
                this.$axios.post('/sys/user/role/' + this.roleForm.id, roleIds).then(res => {
                    this.$message({
                        showClose: true,
                        message: '恭喜你，操作成功',
                        type: 'success',
                        onClose:() => {
                            this.getUserList()
                        }
                    });

                    this.roleDialogFormVisible = false
                })
            },
            // 重置用户密码
            repassHandle(id, username) {
                // 冒泡弹框提示确定操作
                this.$confirm('将重置用户【' + username + '】的密码, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // 携带id发送post请求
                    this.$axios.post("/sys/user/repass", id).then(res => {
                        this.$message({
                            showClose: true,
                            message: '恭喜你，操作成功',
                            type: 'success',
                            onClose: () => {
                            }
                        });
                    })
                })
            }
        }
    }
</script>

<style scoped>
    .el-pagination {
        float: right;
        margin-top: 22px;
    }
</style>