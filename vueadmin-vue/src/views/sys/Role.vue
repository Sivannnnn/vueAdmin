<template>
    <div>
        <el-form :inline="true">
            <el-form-item>
                <el-input
                     v-model="searchForm.name"
                     aria-placeholder="名称"
                     clearable
                 >
                </el-input>
            </el-form-item>
            <el-form-item>
                <el-button @click="getRoleList">搜索</el-button>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="dialogVisible = true">新增</el-button>
            </el-form-item>
            <el-form-item>
                <el-popconfirm title="这是确定批量删除吗？" @confirm="delHandle(null)">
                    <el-button slot="reference" type="danger" :disabled="delBtlStatu">批量删除</el-button>
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
                    prop="name"
                    label="名称"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="code"
                    label="唯一编码"
                    show-overflow-tooltip>
            </el-table-column>
            <el-table-column
                    prop="remark"
                    label="描述"
                    show-overflow-tooltip>
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
                    prop="orderNum"
                    label="操作">
                <template slot-scope="scope">
                    <el-button type="text" @click="permHandle(scope.row.id)">分配权限</el-button>
                    <el-divider direction="vertical"></el-divider>
                    <el-button type="text" @click="editHandle(scope.row.id)">编辑</el-button>
                    <el-divider direction="vertical"></el-divider>
                    <template>
                        <el-popconfirm title="这是一段内容确定删除吗？" @confirm="delHandle(scope.row.id)">
                            <el-button slot="reference" type="text">删除</el-button>
                        </el-popconfirm>
                    </template>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                layout="total, sizes, prev, pager, next, jumper"
                :page-sizes="[10, 20, 50, 100]"
                :current-page="current"
                :page-size="size"
                :total="total">
        </el-pagination>
        <!--新增对话框-->
        <el-dialog
                title="提示"
                :visible.sync="dialogVisible"
                width="600px"
                :before-close="handleClose">
            <el-form :model="editForm" :rules="editFormRules" ref="editForm">
                <el-form-item label="菜单名称" prop="name" label-width="100px">
                    <el-input v-model="editForm.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="唯一编码" prop="code" label-width="100px">
                    <el-input v-model="editForm.code" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" prop="remark" label-width="100px">
                    <el-input v-model="editForm.remark" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="状态" prop="statu" label-width="100px">
                    <el-radio-group v-model="editForm.statu">
                        <el-radio :label=0>禁用</el-radio>
                        <el-radio :label=1>正常</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="resetForm('editForm')">取 消</el-button>
                <el-button type="primary" @click="submitEditForm('editForm')">确 定</el-button>
            </div>
        </el-dialog>
        <!--  分配权限对话框-->
        <el-dialog
                title="分配权限"
                :visible.sync="permDialogVisible"
                width="600px">
            <el-form :model="permForm">
                <el-tree
                        show-checkbox
                        default-expand-all
                        check-strictly
                        :data="permTreeData"
                        node-key="id"
                        :props="defaultProps"
                        ref="permTree">
                </el-tree>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="permDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitPermFormHandle('permForm')">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "Role",
        data() {
            return {
                // 搜索框
                searchForm: {},
                delBtlStatu: true,  // 批量删除disabled激活状态
                total: 0,   // 数据总数
                size: 10,   // 查看列表数量
                current: 1, //当前页数
                dialogVisible: false,   // 删除对话框
                editForm: {},   // 编辑表单数据
                tableData: [],  // 用户列表数据
                // 编辑表单校对规则
                editFormRules: {
                    name: [
                        {required: true, message: '请输入名称', trigger: 'blur'}
                    ],
                    code: [
                        {required: true, message: '请输入唯一编码', trigger: 'blur'}
                    ],
                    statu: [
                        {required: true, message: '请选择状态', trigger: 'blur'}
                    ]
                },
                multipleSelection: [],    // 多选数组
                permDialogVisible: false,   // 分配权限对话框
                permForm: {},    // 分配权限表单
                // 标签节点对象
                defaultProps: {
                    children: 'children',
                    label: 'name'
                },
                permTreeData: [],   // 权限列表数据
            }
        },
        methods: {
            toggleSelection(rows) {
                if (rows) {
                    rows.forEach(row => {
                        this.$refs.multipleTable.toggleRowSelection(row);
                    });
                } else {
                    this.$refs.multipleTable.clearSelection();
                }
            },
            // 角色列表多选操作
            handleSelectionChange(val) {
                console.log("勾选");
                console.log(val)
                // 筛选后将该id存在多选数组
                this.multipleSelection = val
                // 如果列表有被选中，通过多选数组的长度来判断是否 批量删除 激活效果
                this.delBtlStatu = val.length === 0
            },
            // 修改列表展示数据量
            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
                this.size = val
                this.getRoleList()
            },
            // 切换当前的页数
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
                this.size = val
                this.getRoleList()
            },
            // 获取角色列表
            getRoleList(){
                // 发送get请求获取角色列表
                this.$axios.get('/sys/role/list',{
                    // 携带搜索框内容，当前页数，和页码大小
                    params: {
                        name: this.searchForm.name,
                        current: this.current,
                        size: this.size
                    }
                }).then(res => {
                    // 请求成功，将服务器返回结果对应赋值设置
                    this.tableData = res.data.data.records
                    this.size = res.data.data.size
                    this.current = res.data.data.current
                    this.total = res.data.data.total
                })
            },
            // 点击编辑打开编辑对话框
            editHandle(id){
                // 发送get请求获取当前角色的数据信息
                this.$axios.get('/sys/role/info/' + id).then(res => {
                    // 请求成功，将服务器返回的结果赋值给编辑列表
                    this.editForm = res.data.data
                    // 打开编辑对话框
                    this.dialogVisible = true
                })
            },
            // 重置
            resetForm(formName){
                this.$refs[formName].resetFields();
                this.dialogVisible = false
                this.editForm = {}
            },
            // 提交编辑表单
            submitEditForm(formName){
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        // 为了减少代码里，实现路径判断公用
                        // 通过 this.editForm.id 是否存在判断 如果存在，那就是更新（update） 否则为创建保存（save）
                        this.$axios.post('/sys/role/' + (this.editForm.id?'update' : 'save'),this.editForm)
                            .then(res => {
                                // 请求成功，消息弹出成功提示
                                this.$message({
                                    showClose: true,
                                    message: '恭喜你，操作成功！',
                                    type: 'success',
                                    onClose: () => {
                                        this.getRoleList()
                                    }
                                });
                                // 关闭对话框
                                this.dialogVisible = false;
                                this.resetForm(formName);
                            })
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
            // 关闭对话框
            handleClose(){
                this.editHandle('editForm')
            },
            // 删除（批量删除和单个删除）
            delHandle(id){
                // 声明ids数组
                var ids = []
                // if判断触发方法传来的参数是否存在
                if (id){
                    // 如果id存在，将id存入ids数组
                    ids.push(id)
                }else{
                    // 否则为不存在，就是批量删除，将用户列表所有的id存入ids数组
                    this.multipleSelection.forEach(row => {
                        ids.push(row.id)
                    })
                }
                // console.log(ids)
                // 发送post请求 携带ids数组
                this.$axios.post('/sys/role/delete',ids).then(res => {
                    // 请求成功，消息弹出成功提示
                    this.$message({
                        showClose: true,
                        message: '恭喜你，操作成功！',
                        type: 'success',
                        onClose: () => {
                            // 重新获取角色数据
                            this.getRoleList()
                        }
                    });
                })
            },
            // 分配权限
            permHandle(id){
                // 打开对话框
                this.permDialogVisible = true
                // 获取默认勾选内容
                this.$axios.get('/sys/role/info/'+ id).then(res => {
                    // 设置默认勾选数据
                    this.$refs.permTree.setCheckedKeys(res.data.data.menuIds)
                    // 设置完成后将设置情况赋予给权限数组
                    this.permForm = res.data.data
                })
            },
            // 提交分配权限表单
            submitPermFormHandle(formName){
                // 获取表单中被勾选的id
                var menuIds = this.$refs.permTree.getCheckedKeys()
                console.log(menuIds)
                // 提交表单发送post请求——给哪个用户修改权限
                this.$axios.post('sys/role/perm/'+this.permForm.id,menuIds).then(res => {
                    // 对话框显示操作成功
                    this.$message({
                        showClose: true,
                        message: '恭喜你，操作成功！',
                        type: 'success',
                        onClose: () => {
                            this.getRoleList()
                        }
                    });
                    // 关闭对话框
                    this.permDialogVisible = false;
                    this.resetForm(formName);
                })
            }
        },
        created() {
            // 调用获取角色数据方法
            this.getRoleList();
            // 发送get请求，获取权限数据
            this.$axios.get('/sys/menu/list').then(res => {
                this.permTreeData = res.data.data
            })
        }
    }
</script>

<style scoped>
.el-pagination{
    float: right;
    margin-top: 22px;
}
</style>