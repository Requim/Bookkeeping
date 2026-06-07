# 智能记账 App 开发周期记录

本文档是本项目唯一阶段状态源。任何 Codex 或开发者接手本仓库时，必须先读取本文档顶部状态块，再根据“当前阶段任务清单”继续工作。

```yaml
current_phase: P4
phase_name: Android 联调
phase_status: in_progress
app_role: thin_client
backend_stack: Python FastAPI
tracking_level: 阶段+任务清单
quality_gate: required
solid_required: true
max_function_effective_lines: 50
public_contract_docs: required
last_updated: 2026-06-07
```

## 项目定位

本项目目标是做一个智能记账系统。Android App 只做空壳/薄客户端，负责采集手机支付相关信息并展示后端结果；Python FastAPI 后端负责调用大模型、解析账单、去重、存储、统计。

目标架构：

```text
Android 空壳 App
  -> 采集支付通知 / 截图 / 手动文本
  -> 调用 Python FastAPI 后端
  -> 展示待确认账单
  -> 用户确认 / 修改 / 忽略

Python FastAPI 后端
  -> 接收原始采集数据
  -> 调用大模型做结构化解析
  -> 执行去重、分类、存储、统计
  -> 返回待确认账单和账本数据
```

重要边界：

- App 不直接调用大模型 API。
- App 不持有大模型 API Key。
- App 不把本地 Room/规则解析作为主路径继续扩展。
- 当前已有本地优先 MVP 代码只作为历史实现、fallback 或测试参考。

## 阶段状态协议

Codex 判断当前阶段时，只看顶部 YAML 状态块和阶段总览表：

- `current_phase` 决定当前阶段。
- `phase_status` 只能是 `pending`、`in_progress`、`blocked`、`done`。
- 阶段总览表中只能有一个 `in_progress` 阶段。
- 当前阶段完成后，必须同时更新顶部状态块、阶段总览表、当前阶段任务清单。
- 如果阶段被阻塞，必须把 `phase_status` 改为 `blocked`，并在“阻塞记录”中写明原因和需要用户提供的信息。
- 不允许跳过阶段；如果确实跳过，必须在“阶段变更记录”中写明原因。
- 任何阶段完成前必须通过“阶段质量门禁”；未通过时不得把阶段标记为 `done`。

## 阶段总览

| 阶段 | 状态 | 目标 | 完成标准 |
| --- | --- | --- | --- |
| P0 - 需求确认 | done | 明确产品目标、平台、隐私策略、大模型使用方式 | 确认 Android App 为空壳客户端，后端调用大模型 |
| P1 - 空壳 App 架构调整 | done | 把 App 主路径从本地解析改为调用 FastAPI | README 明确 API 契约，App 主流程计划指向远程服务 |
| P2 - FastAPI 后端 MVP | done | 建立后端服务和基础账单接口 | 后端可本地启动，接口返回稳定 JSON |
| P3 - 大模型解析链路 | done | 后端封装 AI 解析服务 | 微信、支付宝、银行通知样例可解析为结构化草稿 |
| P4 - Android 联调 | in_progress | Android App 调用 FastAPI 完成主流程 | 手机或模拟器能走通采集到确认入账 |
| P5 - 端到端验收 | pending | 覆盖通知、截图、手动文本三种入口 | 核心流程可重复演示 |
| P6 - 稳定性与安全 | pending | 处理鉴权、密钥、错误、重试、日志 | 隐私和失败路径有明确处理 |
| P7 - 打包发布 | pending | 生成 APK 并补齐发布材料 | APK 可安装并完成核心验收 |

## 当前阶段：P4 - Android 联调

当前阶段目标：让 Android 空壳 App 与本地 FastAPI 后端完成联调，验证待确认列表、确认、忽略、截图 OCR、手动文本和通知入口都能走后端主路径。

### P4 任务清单

- [ ] 启动 FastAPI 后端并记录本地联调地址。
- [ ] Android 默认后端地址与模拟器访问地址保持一致。
- [ ] 待确认列表来自后端并可刷新。
- [ ] 确认、修改、忽略操作同步到后端。
- [ ] 截图 OCR、手动文本、通知入口都上传后端。
- [ ] 添加或校验网络错误 UI 状态。
- [ ] 手机或模拟器完成一次采集到确认入账联调。
- [ ] 通过函数 50 行、public 契约注释、依赖方向质量门禁。

### P4 完成标准

- Android App 可连接本地 FastAPI。
- 手机或模拟器能走通“上传采集数据 -> 待确认 -> 确认入账 -> 今日汇总更新”。
- 网络失败时 UI 有明确提示。
- README 记录 P4 测试命令、结果和阶段质量门禁结论。

## 已完成阶段记录

### P1 - 空壳 App 架构调整

- [x] 定义 Android 调 FastAPI 的 DTO。
- [x] 定义远程 Repository 接口和远程网关。
- [x] 新增 Ktor Client 网络层。
- [x] 将通知、截图、手动文本主路径调整为调用远程服务。
- [x] 保留通知监听、截图选择、手动文本入口。
- [x] 明确本地 Room/规则解析仅作为 fallback 或测试参考。
- [x] 在 README 中记录 FastAPI 后端接口契约。

P1 质量记录：

- 已执行：函数有效行数检查，通过。
- 已执行：新增 public 契约注释检查，通过。
- 已执行：Android `domain` 层 Android framework import 检查，通过。
- 已执行：`git diff --check`，通过。
- 未执行：Android 单元测试和 `assembleDebug`，原因是当前环境未检测到 `java`、`gradle`，且仓库没有 `gradlew.bat`。

### P2 - FastAPI 后端 MVP

- [x] 建立 `backend/` 目录。
- [x] 按 `api / application / domain / infrastructure` 创建 FastAPI 分层。
- [x] 创建 FastAPI 应用入口：`backend/app/main.py`。
- [x] 实现 `captures`、`drafts`、`transactions`、`summary` 接口。
- [x] 先使用内存存储，后续阶段再替换 SQLite 或正式数据库。
- [x] 提供本地启动命令和接口示例：`backend/README.md`。
- [x] 补充后端测试，并在 README 记录测试结果。
- [x] 通过函数 50 行、public 契约注释、依赖方向质量门禁。

P2 质量记录：

- 已执行：`python -m pip install -r requirements-dev.txt`，通过。
- 已执行：`python -m pytest`，5 passed。
- 已执行：`python -c "from app.main import create_app; app=create_app(); print(len(app.routes))"`，通过。
- 已执行：启动 `python -m uvicorn app.main:app --host 127.0.0.1 --port 8000` 后请求 `/api/summary/today`，返回稳定 JSON。
- 已执行：Python 函数有效行数检查，通过。
- 已执行：Python public 契约 docstring 检查，通过。
- 已执行：FastAPI `domain` 层框架/SDK 依赖检查，通过。
- 已执行：`git diff --check`，通过。
- 临时 fallback：`SimpleExpenseDraftParser` 仅用于 P2 无密钥闭环，P3 接入大模型解析服务后保留为显式 fallback 或按测试结论移除。

### P3 - 大模型解析链路

- [x] 定义 AI 解析服务接口和配置对象。
- [x] 大模型 API Key 只从后端环境变量读取：`LEDGER_AI_API_KEY`。
- [x] 定义结构化输出 schema，并限制模型只返回账单草稿字段。
- [x] 后端封装 OpenAI-compatible 大模型客户端，route 不直接拼 prompt 或调 SDK。
- [x] 覆盖微信、支付宝、银行通知样例。
- [x] 保留 P2 临时规则解析器作为无密钥 fallback。
- [x] 补充大模型解析链路测试，使用 fake client 覆盖解析适配器。
- [x] 通过函数 50 行、public 契约注释、依赖方向质量门禁。

P3 质量记录：

- 已执行：`python -m pytest`，9 passed。
- 已执行：`python -c "from app.infrastructure.container import BackendContainer; c=BackendContainer(); print(type(c.parser).__name__)"`，确认主解析器为 `AiExpenseDraftParser`。
- 已执行：Python 函数有效行数检查，通过。
- 已执行：Python public 契约 docstring 检查，通过。
- 已执行：FastAPI `domain` 层框架/SDK 依赖检查，通过。
- 已执行：API route AI 细节检查，通过，route 中不包含 prompt、SDK 调用或 API Key。
- 大模型配置：后端读取 `LEDGER_AI_API_KEY`、`LEDGER_AI_BASE_URL`、`LEDGER_AI_MODEL`、`LEDGER_AI_TIMEOUT_SECONDS`；未设置密钥时使用 `SimpleExpenseDraftParser` fallback。
- fallback 移除条件：P5 端到端验收确认大模型解析稳定、失败路径和低置信度处理明确后，再决定保留为兜底或移除。

## 当前代码状态

仓库已有一版本地优先 MVP，主要内容包括：

- Android Kotlin/Compose 项目骨架。
- 分层目录：`presentation / domain / data / platform`。
- 通知监听服务：`PaymentNotificationListenerService`。
- 截图选择与 ML Kit OCR 适配器。
- 手动文本识别入口。
- Room 本地表：`raw_captures`、`transaction_drafts`、`transactions`、`categories`、`category_rules`。
- 本地规则解析器：`ChinesePaymentTextParser`。
- 本地分类器：`RuleBasedExpenseClassifier`。
- 去重器：`ShaDuplicateDetector`。
- 待确认账单 UI：确认、修改、忽略。
- 单元测试覆盖解析、分类、去重和 UseCase。
- Ktor Client 远程网络层：`LedgerApiClient`、远程 DTO、远程网关、远程读仓库。
- Android 主路径已切到 FastAPI：通知、截图 OCR、手动文本入口会上传到后端。
- FastAPI 后端 MVP：`backend/` 已包含 `api / application / domain / infrastructure` 分层、内存存储、临时解析器和 API 测试。
- 后端 AI 解析链路：已封装 OpenAI-compatible 客户端、prompt builder、AI-first parser 和无密钥 fallback。

后续开发必须注意：这些本地实现不是新的主方向。新主方向是 App 采集后调用后端，由后端完成大模型解析、去重、存储、统计。

## App 与后端职责边界

### Android App 负责

- 请求和引导通知监听权限。
- 采集支付通知文本。
- 选择截图或账单图片。
- 提供手动粘贴文本入口。
- 调用 FastAPI 后端。
- 展示待确认账单、最近账单、今日汇总。
- 发起确认、修改、忽略操作。
- 展示网络错误和低置信度提示。

### FastAPI 后端负责

- 接收 App 上传的采集数据。
- 调用大模型 API。
- 把原始文本或 OCR 结果解析为结构化账单草稿。
- 执行去重、分类、状态流转。
- 存储正式账单和草稿。
- 提供今日汇总和账单查询。
- 管理大模型 API Key 和后端鉴权。

## FastAPI API 草案

### 上传采集数据

```http
POST /api/captures
```

请求：

```json
{
  "source": "NOTIFICATION",
  "appPackage": "com.tencent.mm",
  "title": "微信支付",
  "text": "商户：瑞幸咖啡 支付成功 ¥18.50",
  "imageBase64": null,
  "capturedAt": 1780819200000
}
```

响应：

```json
{
  "draftId": "draft_001",
  "amountCents": 1850,
  "currency": "CNY",
  "type": "EXPENSE",
  "merchant": "瑞幸咖啡",
  "category": "餐饮",
  "paidAt": 1780819200000,
  "confidence": 0.93,
  "status": "PENDING"
}
```

### 查询待确认草稿

```http
GET /api/drafts?status=PENDING
```

响应：

```json
{
  "items": [
    {
      "draftId": "draft_001",
      "amountCents": 1850,
      "currency": "CNY",
      "type": "EXPENSE",
      "merchant": "瑞幸咖啡",
      "category": "餐饮",
      "paidAt": 1780819200000,
      "confidence": 0.93,
      "status": "PENDING"
    }
  ]
}
```

### 确认草稿

```http
PATCH /api/drafts/{id}/confirm
```

请求：

```json
{
  "amountCents": 1850,
  "currency": "CNY",
  "type": "EXPENSE",
  "merchant": "瑞幸咖啡",
  "category": "餐饮",
  "paidAt": 1780819200000,
  "note": ""
}
```

响应：

```json
{
  "transactionId": "txn_001",
  "status": "CONFIRMED"
}
```

### 忽略草稿

```http
PATCH /api/drafts/{id}/ignore
```

响应：

```json
{
  "draftId": "draft_001",
  "status": "IGNORED"
}
```

### 查询最近账单

```http
GET /api/transactions?limit=20
```

响应：

```json
{
  "items": [
    {
      "transactionId": "txn_001",
      "amountCents": 1850,
      "currency": "CNY",
      "type": "EXPENSE",
      "merchant": "瑞幸咖啡",
      "category": "餐饮",
      "paidAt": 1780819200000,
      "note": ""
    }
  ]
}
```

### 查询今日汇总

```http
GET /api/summary/today
```

响应：

```json
{
  "expenseCents": 1850,
  "currency": "CNY",
  "pendingDraftCount": 1
}
```

## 后续阶段任务概要

### P2 - FastAPI 后端 MVP

- [ ] 建立 `backend/` 目录。
- [ ] 创建 FastAPI 应用入口。
- [ ] 实现 captures、drafts、transactions、summary 接口。
- [ ] 先使用内存或 SQLite 存储。
- [ ] 提供本地启动命令和接口示例。

### P3 - 大模型解析链路

- [ ] 后端新增 AI 解析服务接口。
- [ ] 大模型 API Key 只从后端环境变量读取。
- [ ] 定义结构化输出 schema。
- [ ] 覆盖微信、支付宝、银行通知样例。

### P4 - Android 联调

- [ ] Android 新增网络层。
- [ ] 待确认列表改为后端数据。
- [ ] 确认、修改、忽略同步到后端。
- [ ] 添加网络错误 UI 状态。

### P5 - 端到端验收

- [ ] 验证通知采集入口。
- [ ] 验证截图入口。
- [ ] 验证手动文本入口。
- [ ] 验证重复记录、网络失败、低置信度场景。

### P6 - 稳定性与安全

- [ ] 后端鉴权。
- [ ] API Key 环境变量管理。
- [ ] 后端错误日志。
- [ ] App 失败重试或本地暂存策略。

### P7 - 打包发布

- [ ] 生成 debug APK。
- [ ] 生成 release APK。
- [ ] 补齐权限说明。
- [ ] 补齐隐私说明。
- [ ] 记录安装和验收步骤。

## 全阶段工程纪律

用户提到的 `SLOID` 统一按 `SOLID` 原则执行。所有阶段的 Android、后端、测试、脚本代码都必须遵守本节规则，目标是降低模块耦合、防止代码腐化，并让后续 Codex 能稳定接手。

### SOLID 与低耦合规则

- 单一职责：一个类、函数、模块只承担一个清晰职责。
- 开闭原则：新增支付来源、解析方式、存储实现、大模型供应商时，优先新增实现而不是修改核心流程。
- 里氏替换：接口实现必须能被调用方无感替换，不能依赖隐藏副作用。
- 接口隔离：不要设计大而全接口；Repository、UseCase、Service、Client、Parser 应按能力拆小。
- 依赖倒置：业务层依赖接口或协议，不依赖框架、数据库 SDK、大模型 SDK 的具体实现。

### Android 分层规则

- Android 继续保持 `presentation / domain / data / platform` 分层。
- `presentation` 只负责 UI 状态、用户交互和调用 ViewModel。
- `domain` 保持纯 Kotlin，不依赖 Android framework。
- `data` 负责 Repository 实现、网络、本地 fallback、DTO 映射。
- `platform` 负责通知监听、截图选择、系统权限、Android 系统能力。
- 禁止 UI 层直接写解析、分类、去重、存储细节。

### FastAPI 后端分层规则

后端必须按 `api / application / domain / infrastructure` 分层：

- `api`：路由、请求/响应 schema、HTTP 状态码映射。
- `application`：用例编排，例如创建采集、确认草稿、查询汇总。
- `domain`：实体、值对象、Repository Protocol、业务规则。
- `infrastructure`：数据库、大模型客户端、外部服务实现。

后端依赖方向：

```text
api -> application -> domain
infrastructure -> domain/application interfaces
domain 不依赖 FastAPI / 数据库 SDK / 大模型 SDK
```

### 强制编码规则

- 函数非注释、非空行不超过 50 行；超过时必须提取函数、类或服务。
- 新增 public 接口、Repository、UseCase、Service、DAO、DTO、Entity、Pydantic Schema 必须写注释。
- FastAPI route 函数必须有 docstring。
- Pydantic request/response schema 必须有字段说明。
- Repository Protocol、Service、UseCase 必须有 docstring。
- 新模块必须说明职责边界，避免大而全类、大而全文件。
- 共享逻辑必须提取到独立函数或服务，禁止复制粘贴扩散。
- Git commit 信息必须使用中文，尽量简短准确。
- App 层不直接持有大模型 API Key。
- 大模型 prompt、解析逻辑不能散落在 route 或 UI 中。
- 数据库存取不能散落在 application/domain 中。
- 任何临时 fallback 必须在 README 标记为临时，并写清移除条件。

### Clean Code 防腐化规则

- 命名必须表达业务含义，避免无意义缩写。
- 函数优先早返回，减少深层嵌套。
- 错误处理必须明确，不吞异常。
- 日志不能泄露 API Key、完整支付截图、银行卡号、身份证号等敏感信息。
- 测试样例可以使用脱敏文本，禁止提交真实个人账单数据。
- 新增抽象必须服务于实际变化点，不为“看起来高级”而抽象。

## 阶段质量门禁

每个阶段标记为 `done` 前，必须完成以下检查：

- README 已更新顶部状态块、阶段总览表、当前阶段任务清单和阶段变更记录。
- 已记录本阶段执行过的测试命令、结果和未执行原因。
- 已确认函数非注释、非空行不超过 50 行。
- 已确认新增 public 契约均有注释。
- 已确认 Android `domain` 层不依赖 Android framework。
- 已确认 FastAPI `domain` 层不依赖 FastAPI、数据库 SDK、大模型 SDK。
- 已确认 UI/API handler 未直接承载解析、去重、分类、存储细节。
- 如检查无法执行，必须在“阻塞记录”或“运行状态”中写明原因。

违反以上任一项时，不允许把阶段状态改为 `done`。

## 运行状态

当前环境检测结果：

- 未检测到 `java`。
- 未检测到 `gradle`。
- 仓库还没有 `gradlew.bat`。
- 已检测到 Python 3.13。
- FastAPI 后端依赖可通过 `backend/requirements-dev.txt` 安装。

因此当前环境无法直接运行：

```powershell
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

需要先用 Android Studio 打开项目，安装 JDK 17、Android SDK 35，并生成或同步 Gradle Wrapper。

后端可本地运行：

```powershell
cd backend
python -m pip install -r requirements-dev.txt
python -m pytest
python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

后端大模型环境变量：

```powershell
$env:LEDGER_AI_API_KEY = "只放在后端环境变量中"
$env:LEDGER_AI_BASE_URL = "https://api.openai.com/v1"
$env:LEDGER_AI_MODEL = "gpt-4o-mini"
```

## README 文档验收标准

- 顶部 YAML 状态块存在。
- 状态块包含 `current_phase`、`phase_status`、`backend_stack`。
- 状态块包含 `quality_gate`、`solid_required`、`max_function_effective_lines`、`public_contract_docs`。
- 阶段总览表中只有一个 `in_progress` 阶段。
- 当前阶段任务清单与 `current_phase` 一致。
- 存在“全阶段工程纪律”和“阶段质量门禁”。
- 明确 FastAPI 后端分层和依赖方向。
- 明确函数 50 行限制和 public 契约注释要求。
- 明确违反质量门禁时不能标记阶段完成。
- 本文档明确写明 Codex 接手时必须先读 README。
- 本文档明确写明 App 是薄客户端，FastAPI 后端负责大模型调用。

## 阻塞记录

当前无阻塞。

## 阶段变更记录

- 2026-06-07：确认项目方向从“本地优先 Android MVP”切换为“Android 空壳 App + Python FastAPI 后端 + 大模型解析”。
- 2026-06-07：将当前阶段设为 `P1 - 空壳 App 架构调整`，状态为 `in_progress`。
- 2026-06-07：新增全阶段工程纪律和阶段质量门禁，要求后续 Codex 编码时遵循 SOLID、低耦合、接口注释、函数 50 行限制和 Clean Code。
- 2026-06-07：完成 P1，Android 增加 Ktor 远程网络层，通知、截图和手动文本主路径切换为 FastAPI 调用。
- 2026-06-07：进入 `P2 - FastAPI 后端 MVP`，下一步建立 `backend/` 并实现基础账单接口。
- 2026-06-07：完成 P2，新增 FastAPI 后端 MVP、内存仓库、临时规则解析器、API 测试和本地启动说明。
- 2026-06-07：进入 `P3 - 大模型解析链路`，下一步封装后端 AI 解析服务并替换临时解析器主路径。
- 2026-06-07：完成 P3，新增后端 AI 解析协议、OpenAI-compatible 客户端、prompt builder、AI-first parser 和 fake client 测试。
- 2026-06-07：进入 `P4 - Android 联调`，下一步验证 Android 与本地 FastAPI 的端到端主流程。

## 给后续 Codex 的接手规则

1. 先读取 README 顶部 YAML 状态块。
2. 先读取“全阶段工程纪律”和“阶段质量门禁”。
3. 根据 `current_phase` 找到当前阶段任务清单。
4. 如果继续实现，应优先完成当前阶段任务，不要跳阶段。
5. 若新增后端代码，必须按 FastAPI `api / application / domain / infrastructure` 分层落位。
6. 若新增接口未注释或函数超过 50 行，不允许标记阶段完成。
7. 提交代码时，commit 信息必须使用中文，尽量简短准确。
8. 如果完成阶段，必须更新顶部状态块、阶段总览表、当前阶段任务清单和阶段变更记录。
9. 如果遇到阻塞，必须把 `phase_status` 改为 `blocked`，并写入“阻塞记录”。
10. 不要继续把本地 Room/规则解析当作主路径扩展。
11. 大模型调用只能放在 Python FastAPI 后端，不能放进 Android App。
