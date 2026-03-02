/**
 * 身份证处理工具函数
 * 提供身份证号码的格式化、校验和信息提取功能
 */

// 身份证校验位权重
const ID_CARD_WEIGHTS = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
// 身份证校验位对应表
const ID_CARD_CHECK_CODES = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']

/**
 * 格式化身份证号码
 * 去除空格，转换为大写
 * @param {string} idCard 原始身份证号码
 * @returns {string} 格式化后的身份证号码
 */
export function formatIdCard(idCard) {
  if (!idCard) return ''
  
  // 去除所有空格并转换为大写
  return idCard.replace(/\s/g, '').toUpperCase()
}

/**
 * 校验身份证号码格式
 * @param {string} idCard 身份证号码
 * @returns {boolean} 是否为有效格式
 */
export function validateIdCardFormat(idCard) {
  if (!idCard) return false
  
  // 检查长度和基本格式
  const idCardRegex = /^[0-9]{17}[0-9X]$/
  return idCardRegex.test(idCard)
}

/**
 * 计算身份证校验位
 * @param {string} idCard17 身份证前17位
 * @returns {string} 校验位
 */
function calculateCheckCode(idCard17) {
  let sum = 0
  for (let i = 0; i < 17; i++) {
    sum += parseInt(idCard17.charAt(i)) * ID_CARD_WEIGHTS[i]
  }
  const remainder = sum % 11
  return ID_CARD_CHECK_CODES[remainder]
}

/**
 * 校验身份证校验位是否正确
 * @param {string} idCard 18位身份证号码
 * @returns {object} 校验结果 {valid: boolean, message: string}
 */
export function validateIdCardCheckCode(idCard) {
  if (!idCard || idCard.length !== 18) {
    return {
      valid: false,
      message: '身份证号码长度不正确'
    }
  }
  
  if (!validateIdCardFormat(idCard)) {
    return {
      valid: false,
      message: '身份证号码格式不正确'
    }
  }
  
  const idCard17 = idCard.substring(0, 17)
  const checkCode = idCard.charAt(17)
  const calculatedCheckCode = calculateCheckCode(idCard17)
  
  if (checkCode === calculatedCheckCode) {
    return {
      valid: true,
      message: '身份证号码校验正确'
    }
  } else {
    return {
      valid: false,
      message: '身份证号码无效'
    }
  }
}

/**
 * 从身份证号码中提取生日
 * @param {string} idCard 18位身份证号码
 * @returns {string|null} 生日字符串 (YYYY-MM-DD) 或 null
 */
export function extractBirthdayFromIdCard(idCard) {
  if (!idCard || idCard.length !== 18) {
    return null
  }
  
  try {
    const year = idCard.substring(6, 10)
    const month = idCard.substring(10, 12)
    const day = idCard.substring(12, 14)
    
    // 验证日期有效性
    const date = new Date(year, month - 1, day)
    if (date.getFullYear() == year && 
        date.getMonth() == (month - 1) && 
        date.getDate() == day) {
      return `${year}-${month}-${day}`
    }
    
    return null
  } catch (error) {
    console.error('提取生日失败:', error)
    return null
  }
}

/**
 * 从身份证号码中提取性别
 * @param {string} idCard 18位身份证号码
 * @returns {string|null} 性别代码 ('1' 男性, '2' 女性) 或 null
 */
export function extractGenderFromIdCard(idCard) {
  if (!idCard || idCard.length !== 18) {
    return null
  }
  
  try {
    // 身份证号码倒数第二位（第17位，索引16）为性别码
    // 奇数为男性，偶数为女性
    const genderCode = parseInt(idCard.charAt(16))
    return genderCode % 2 === 1 ? '1' : '2'  // 1-男性, 2-女性
  } catch (error) {
    console.error('提取性别失败:', error)
    return null
  }
}

/**
 * 从身份证号码中提取所有信息
 * @param {string} idCard 身份证号码
 * @returns {object} 提取的信息
 */
export function extractInfoFromIdCard(idCard) {
  // 先格式化身份证号码
  const formattedIdCard = formatIdCard(idCard)
  
  // 校验身份证
  const checkResult = validateIdCardCheckCode(formattedIdCard)
  
  // 如果身份证无效，不提取生日和性别信息
  if (!checkResult.valid) {
    return {
      formattedIdCard,
      checkResult,
      birthday: null,
      gender: null,
      isValid: false
    }
  }
  
  // 身份证有效时才提取信息
  const birthday = extractBirthdayFromIdCard(formattedIdCard)
  const gender = extractGenderFromIdCard(formattedIdCard)
  
  return {
    formattedIdCard,
    checkResult,
    birthday,
    gender,
    isValid: true
  }
}

/**
 * 身份证输入处理函数
 * 用于在输入框的input事件中调用
 * @param {string} value 输入的值
 * @returns {string} 处理后的值
 */
export function handleIdCardInput(value) {
  return formatIdCard(value)
}

/**
 * 身份证失焦处理函数
 * 用于在输入框的blur事件中调用，返回完整的处理结果
 * @param {string} idCard 身份证号码
 * @param {object} options 选项 {showWarning: boolean} 是否显示警告信息
 * @returns {object} 处理结果
 */
export function handleIdCardBlur(idCard, options = {}) {
  const { showWarning = true } = options
  const result = extractInfoFromIdCard(idCard)
  
  // 如果需要显示警告且校验失败
  if (showWarning && result.formattedIdCard && !result.checkResult.valid) {
    // 这里可以集成到Vue的message组件
    console.warn('身份证校验提示:', result.checkResult.message)
  }
  
  return result
}
