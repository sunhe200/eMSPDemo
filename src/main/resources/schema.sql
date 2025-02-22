-- 删除旧表（如果存在）
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS card;

-- 创建账户表
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,             -- 账户的唯一标识
    contract_id VARCHAR(255) NOT NULL UNIQUE,       -- 使用 EMAID 生成算法生成的合同ID，必须唯一
    status VARCHAR(50),                             -- 账户状态 (CREATED, ACTIVATED, DEACTIVATED)
    last_updated TIMESTAMP,                         -- 最后更新时间
    editor VARCHAR(50)                              -- 更新人
);

-- 创建卡片表
CREATE TABLE card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(16) NOT NULL UNIQUE,        -- visible_number, 随机数字作为卡号，必须唯一
    status VARCHAR(50),                             -- 卡片状态 (CREATED, ASSIGNED, ACTIVATED, DEACTIVATED)
    account_id BIGINT,                              -- 关联的账户ID（可为空）
    uid VARCHAR(255) NOT NULL UNIQUE,               -- RFID 的 UID，必须唯一
    last_updated TIMESTAMP,                         -- 最后更新时间
    editor VARCHAR(50)                              -- 更新人
);
