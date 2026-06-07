# 后端本地启动

本目录是 P2 阶段 FastAPI 后端 MVP。当前使用内存存储和临时规则解析器，P3 会把解析器替换为大模型解析服务。

```powershell
cd backend
python -m pip install -r requirements-dev.txt
python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

接口根路径为 `http://127.0.0.1:8000/api`，Android 模拟器默认通过 `http://10.0.2.2:8000` 访问宿主机。
