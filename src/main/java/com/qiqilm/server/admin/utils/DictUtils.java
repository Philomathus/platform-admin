package com.qiqilm.server.admin.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.domain.SysDictData;
import com.qiqilm.server.admin.mapper.SysDictDataMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 字典工具类
 *
 * @author 77tv
 */
@Log4j2
public class DictUtils {
	/**
	 * 分隔符
	 */
	public static final String SEPARATOR = ",";

	/**
	 * 设置字典缓存
	 *
	 * @param key       参数键
	 * @param dictDatas 字典数据列表
	 */
	public static void setDictCache( String key, List<SysDictData> dictDatas ) {
		SpringUtils.getBean( RedisUtil.class ).strSet( getCacheKey( key ), JsonUtil.object2Json( dictDatas ) );
	}

	/**
	 * 获取字典缓存
	 *
	 * @param key 参数键
	 * @return dictDatas 字典数据列表
	 */
	public static List<SysDictData> getDictCache( String key ) {
		if ( !SpringUtils.getBean( RedisUtil.class ).exists( getCacheKey( key ) ) ) {
			List<SysDictData> dictDataList = SpringUtils.getBean( SysDictDataMapper.class )
					.selectDictDataByType( getCacheKey( key ) );
			log.warn( "{}", JsonUtil.object2Json( dictDataList ) );
			if ( !CollectionUtils.isEmpty( dictDataList ) ) {
				setDictCache( getCacheKey( key ), dictDataList );
			}
		}
		String cacheObj = SpringUtils.getBean( RedisUtil.class ).strGet( getCacheKey( key ) );
		if ( StringUtils.isNotNull( cacheObj ) ) {
			return JsonUtil.json2Array( cacheObj, new TypeReference<List<SysDictData>>() {} );
		}
		return null;
	}

	/**
	 * 根据字典类型和字典值获取字典标签
	 *
	 * @param dictType  字典类型
	 * @param dictValue 字典值
	 * @return 字典标签
	 */
	public static String getDictLabel( String dictType, String dictValue ) {
		return getDictLabel( dictType, dictValue, SEPARATOR );
	}

	/**
	 * 根据字典类型和字典标签获取字典值
	 *
	 * @param dictType  字典类型
	 * @param dictLabel 字典标签
	 * @return 字典值
	 */
	public static String getDictValue( String dictType, String dictLabel ) {
		return getDictValue( dictType, dictLabel, SEPARATOR );
	}

	/**
	 * 根据字典类型和字典值获取字典标签
	 *
	 * @param dictType  字典类型
	 * @param dictValue 字典值
	 * @param separator 分隔符
	 * @return 字典标签
	 */
	public static String getDictLabel( String dictType, String dictValue, String separator ) {
		StringBuilder     propertyString = new StringBuilder();
		List<SysDictData> datas          = getDictCache( dictType );

		if ( StringUtils.containsAny( separator, dictValue ) && StringUtils.isNotEmpty( datas ) ) {
			for ( SysDictData dict : datas ) {
				for ( String value : dictValue.split( separator ) ) {
					if ( value.equals( dict.getDictValue() ) ) {
						propertyString.append( dict.getDictLabel() + separator );
						break;
					}
				}
			}
		} else {
			for ( SysDictData dict : datas ) {
				if ( dictValue.equals( dict.getDictValue() ) ) {
					return dict.getDictLabel();
				}
			}
		}
		return StringUtils.stripEnd( propertyString.toString(), separator );
	}

	/**
	 * 根据字典类型和字典标签获取字典值
	 *
	 * @param dictType  字典类型
	 * @param dictLabel 字典标签
	 * @param separator 分隔符
	 * @return 字典值
	 */
	public static String getDictValue( String dictType, String dictLabel, String separator ) {
		StringBuilder     propertyString = new StringBuilder();
		List<SysDictData> datas          = getDictCache( dictType );

		if ( StringUtils.containsAny( separator, dictLabel ) && StringUtils.isNotEmpty( datas ) ) {
			for ( SysDictData dict : datas ) {
				for ( String label : dictLabel.split( separator ) ) {
					if ( label.equals( dict.getDictLabel() ) ) {
						propertyString.append( dict.getDictValue() + separator );
						break;
					}
				}
			}
		} else {
			for ( SysDictData dict : datas ) {
				if ( dictLabel.equals( dict.getDictLabel() ) ) {
					return dict.getDictValue();
				}
			}
		}
		return StringUtils.stripEnd( propertyString.toString(), separator );
	}

	/**
	 * 清空字典缓存
	 */
	public static void clearDictCache() {
		Collection<String> keys = SpringUtils.getBean( StringRedisTemplate.class )
				.execute( ( RedisCallback<List<String>> ) connection -> {
					List<String> resultList = new ArrayList<>();
					Cursor<byte[]> cursor = connection.scan( ScanOptions.scanOptions()
							.match( AdminConstants.SYS_DICT_KEY + "*" ).count( 5 ).build() );
					while ( cursor.hasNext() ) {
						String key = new String( cursor.next() );
						resultList.add( key );
					}
					return resultList;
				} );
		SpringUtils.getBean( RedisUtil.class ).unlink( keys );
	}

	/**
	 * 设置cache key
	 *
	 * @param configKey 参数键
	 * @return 缓存键key
	 */
	public static String getCacheKey( String configKey ) {
		return AdminConstants.SYS_DICT_KEY + configKey;
	}
}
