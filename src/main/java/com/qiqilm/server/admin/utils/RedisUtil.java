package com.qiqilm.server.admin.utils;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.enums.EnumLock;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings( "unused" )
@Component
@Log4j2
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // ---------------------- key操作 ---------------------

    @Deprecated
    public Collection<String> keys( String s ) {
        return stringRedisTemplate.keys( s );
    }

    /**
     * 给key附加过期时间
     */
    public Boolean expire( String key, Duration timeout ) {
        return stringRedisTemplate.expire( key, timeout.toMillis(), TimeUnit.MILLISECONDS );
    }

    /**
     * 给key指定到期时间
     */
    public Boolean expireAt( String key, Date expireAt ) {
        return stringRedisTemplate.expireAt( key, expireAt );
    }

    /**
     * 给key指定到期时间
     */
    public Boolean expireAt( String key, Instant expireAt ) {
        return stringRedisTemplate.expireAt( key, expireAt );
    }

    /**
     * 移除指定key的过期时间
     */
    public Boolean persist( String key ) {
        return stringRedisTemplate.persist( key );
    }

    /**
     * 获取指定key的过期时间
     */
    public Long getExpire( String key ) {
        return stringRedisTemplate.getExpire( key );
    }

    /**
     * 修改key名
     */
    public void rename( String key, String newKey ) {
        stringRedisTemplate.rename( key, newKey );
    }

    /**
     * 修改key名，如果key不存在，将报错
     */
    public Boolean renameIfAbsent( String key, String newKey ) {
        return stringRedisTemplate.renameIfAbsent( key, newKey );
    }

    /**
     * 删除一个或多个key-value <br/> 请使用 {@link #unlink}
     */
    @Deprecated
    public Long delete( Collection<String> keys ) {
        return stringRedisTemplate.delete( keys );
    }

    /**
     * 删除一个或多个key-value <br/> 请使用 {@link #unlink}
     */
    @Deprecated
    public Boolean delete( String keys ) {
        return stringRedisTemplate.delete( keys );
    }

    /**
     * 是否存在key
     */
    public Boolean exists( String key ) {
        return stringRedisTemplate.hasKey( key );
    }

    /**
     * 从当前数据库中随机返回一个 key，当数据库为空时，返回null。
     */
    public String randomKey() {
        return stringRedisTemplate.randomKey();
    }

    /**
     * 返回 key 所储存的值的类型，当key不存在时，返回类型是{@link DataType "none"}
     */
    public DataType type( String key ) {
        return stringRedisTemplate.type( key );
    }

    /**
     * 删除一个或多个key-value，但是，相比DEL会产生阻塞，该命令会在另一个线程中回收内存，因此它是非阻塞的。
     */
    public Long unlink( Collection<String> keys ) {
        return stringRedisTemplate.unlink( keys );
    }

    /**
     * 删除一个或多个key-value，但是，相比DEL会产生阻塞，该命令会在另一个线程中回收内存，因此它是非阻塞的。
     */
    public Boolean unlink( String keys ) {
        return stringRedisTemplate.unlink( keys );
    }

    // ---------------------- string操作 ---------------------

    /**
     * 设置 String 类型 key-value
     */
    public void strSet( String key, String value ) {
        stringRedisTemplate.opsForValue().set( key, value );
    }

    /**
     * 设置 String 类型 key-value 并添加过期时间
     *
     * @param timeout 过期时间
     */
    public void strSet( String key, String value, Duration timeout ) {
        stringRedisTemplate.opsForValue().set( key, value, timeout );
    }

    /**
     * 设置 String 类型 key-value，将 value 设置到指定的偏移量上
     *
     * @see <a href="http://doc.redisfans.com/string/setrange.html">Document：SETRANGE</a>
     */
    public void strSetRange( String key, String value, long offset ) {
        stringRedisTemplate.opsForValue().set( key, value, offset );
    }

    /**
     * 只在键 key 不存在的情况下，将键 key 的值设置为 value 。<br/> 若键 key 已经存在，则不做任何动作。
     */
    public Boolean strSetIfAbsent( String key, String value ) {
        return stringRedisTemplate.opsForValue().setIfAbsent( key, value );
    }

    /**
     * 只在键 key 不存在的情况下，将键 key 的值设置为 value 。<br/> 若键 key 已经存在， 则不做任何动作。<br/>添加过期时间
     *
     * @param timeout 过期时间
     */
    public Boolean strSetIfAbsent( String key, String value, Duration timeout ) {
        return stringRedisTemplate.opsForValue().setIfAbsent( key, value, timeout );
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)。 <br/> 当 offset 比字符串值的长度大，或者 key 不存在时，返回false
     *
     * @see <a href="http://doc.redisfans.com/string/getbit.html">Document：GETBIT</a>
     */
    public Boolean strSetBit( String key, long offset, boolean value ) {
        return stringRedisTemplate.opsForValue().setBit( key, offset, value );
    }

    /**
     * 获取 String 类型 key-value
     */
    public String strGet( String key ) {
        return stringRedisTemplate.opsForValue().get( key );
    }

    /**
     * 获取 String 类型 value 指定的偏移量
     *
     * @see <a href="http://doc.redisfans.com/string/getrange.html">Document：GETRANGE</a>
     */
    public String strGetRange( String key, long start, long end ) {
        return stringRedisTemplate.opsForValue().get( key, start, end );
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)。 <br/> 当 offset 比字符串值的长度大，或者 key 不存在时，返回false
     *
     * @see <a href="http://doc.redisfans.com/string/getbit.html">Document：GETBIT</a>
     */
    public Boolean strGetBit( String key, long offset ) {
        return stringRedisTemplate.opsForValue().getBit( key, offset );
    }

    /**
     * 如果 key 存在则覆盖，并返回旧值，如果不存在，返回null并添加
     */
    public String strGetAndSet( String key, String value ) {
        return stringRedisTemplate.opsForValue().getAndSet( key, value );
    }

    /**
     * 把Redis字符串当作位数组，并能对变长位宽和任意未字节对齐的指定整型位域进行寻址。
     *
     * @see <a href="http://www.redis.cn/commands/bitfield.html">Document：BITFIELD</a>
     * @since Redis Version: 3.2.0
     */
    public List<Long> strBitField( String key, BitFieldSubCommands command ) {
        return stringRedisTemplate.opsForValue().bitField( key, command );
    }

    /**
     * 将返回一个列表，列表中包含了所有给定键的值，如果某个键不存在，那么这个键的值将以null表示
     */
    public List<String> strMGet( Collection<String> keys ) {
        return stringRedisTemplate.opsForValue().multiGet( keys );
    }

    /**
     * 批量添加 key-value (重复的键会覆盖)
     */
    public void strMSet( Map<String, String> keyAndValue ) {
        stringRedisTemplate.opsForValue().multiSet( keyAndValue );
    }

    /**
     * 批量添加 key-value 只有在键不存在时，才添加 map 中只要有一个key存在，则全部不添加
     */
    public Boolean strMSetIfAbsent( Map<String, String> keyAndValue ) {
        return stringRedisTemplate.opsForValue().multiSetIfAbsent( keyAndValue );
    }

    /**
     * 对一个 key-value 的值进行加 1 操作，如果该 key 不存在 将创建一个key 并赋值 1 如果 key 储存的值不能被解释为数字，将报错
     */
    public Long strIncrement( String key ) {
        return stringRedisTemplate.opsForValue().increment( key );
    }

    /**
     * 对一个 key-value 的值进行加操作，如果该 key 不存在 将创建一个key 并赋值该 number
     */
    public Long strIncrement( String key, long number ) {
        return stringRedisTemplate.opsForValue().increment( key, number );
    }

    /**
     * 对一个 key-value 的值进行加操作，如果该 key 不存在 将创建一个key 并赋值该 number
     */
    public Double strIncrement( String key, double number ) {
        return stringRedisTemplate.opsForValue().increment( key, number );
    }

    /**
     * 对一个 key-value 的值进行减1操作，如果该 key 不存在 将创建一个key 并赋值1 如果 key 储存的值不能被解释为数字，将报错
     */
    public Long strDecrement( String key ) {
        return stringRedisTemplate.opsForValue().decrement( key );
    }

    /**
     * 对一个 key-value 的值进行加操作，如果该 key 不存在 将创建一个key 并赋值1 如果 key 储存的值不能被解释为数字，将报错
     */
    public Long strDecrement( String key, long number ) {
        return stringRedisTemplate.opsForValue().decrement( key, number );
    }

    /**
     * 对一个 key-value 的值进行追加操作，如果该 key 不存在 将创建一个key 并赋值value，返回value的长度
     */
    public Integer strAppend( String key, String value ) {
        return stringRedisTemplate.opsForValue().append( key, value );
    }

    /**
     * 返回 key-value 的值的长度，如果该 key 不存在，返回 0
     */
    public Long strSize( String key ) {
        return stringRedisTemplate.opsForValue().size( key );
    }

    // ---------------------- hash操作 ---------------------

    /**
     * 添加 Hash 键值对
     */
    public void hSet( String key, Object hashKey, Object value ) {
        stringRedisTemplate.opsForHash().put( key, hashKey, value );
    }

    /**
     * 批量添加 hash 的 键值对 有则覆盖,没有则添加
     */
    public void hMSet( String key, Map<?, ?> map ) {
        stringRedisTemplate.opsForHash().putAll( key, map );
    }

    /**
     * 添加 hash 键值对. 不存在的时候才添加
     */
    public Boolean hSetIfAbsent( String key, Object hashKey, Object value ) {
        return stringRedisTemplate.opsForHash().putIfAbsent( key, hashKey, value );
    }

    /**
     * 删除指定 hash 的 HashKey
     *
     * @return 删除成功的 数量
     */
    public Long hDelete( String key, Object... hashKeys ) {
        return stringRedisTemplate.opsForHash().delete( key, hashKeys );
    }

    /**
     * 给指定 hash 的 hashkey 做加操作
     */
    public Long hIncrement( String key, Object hashKey, long number ) {
        return stringRedisTemplate.opsForHash().increment( key, hashKey, number );
    }

    /**
     * 给指定 hash 的 hashkey 做加Flat操作
     */
    public Double hIncrement( String key, Object hashKey, Double number ) {
        return stringRedisTemplate.opsForHash().increment( key, hashKey, number );
    }

    /**
     * 获取指定 key 下的 hashkey
     */
    public Object hGet( String key, Object hashKey ) {
        return stringRedisTemplate.opsForHash().get( key, hashKey );
    }

    /**
     * 获取指定 key 下的 hashkey 的值，如果 hashkey 不存在，则值会是 null
     */
    public List<Object> hMGet( String key, Collection<Object> hashKeys ) {
        return stringRedisTemplate.opsForHash().multiGet( key, hashKeys );
    }

    /**
     * 获取 key 下的 所有  hashkey 和 value
     */
    public Map<Object, Object> hGetAll( String key ) {
        return stringRedisTemplate.opsForHash().entries( key );
    }

    /**
     * 验证指定 key 下 有没有指定的 hashkey
     */
    public Boolean hExists( String key, Object hashKey ) {
        return stringRedisTemplate.opsForHash().hasKey( key, hashKey );
    }

    /**
     * 获取 key 下的 所有 hashkey 字段名 <br/> 已过期！请使用 {@link #hScan}
     */
    @Deprecated
    public Set<Object> hKeys( String key ) {
        return stringRedisTemplate.opsForHash().keys( key );
    }

    /**
     * 获取指定 hash 下面的 键值对 数量
     */
    public Long hSize( String key ) {
        return stringRedisTemplate.opsForHash().size( key );
    }

    /**
     * 增量迭代 hash 下面的 键值对
     */
    public Cursor<Map.Entry<Object, Object>> hScan( String key, ScanOptions options ) {
        return stringRedisTemplate.opsForHash().scan( key, options );
    }

    // ---------------------- list操作 ---------------------

    /**
     * 指定 list 从左入栈
     *
     * @return 当前队列的长度
     */
    public Long lLeftPush( String key, String value ) {
        return stringRedisTemplate.opsForList().leftPush( key, value );
    }

    /**
     * <p>
     * 将值 value 插入到列表 key 当中，位于值 pivot 之前。<br/> 当 pivot 不存在于列表 key 时，不执行任何操作。<br/> 当 key 不存在时， key 被视为空列表，不执行任何操作。<br/> 如果 key
     * 不是列表类型，返回一个错误。
     * </p>
     *
     * @return 当前队列的长度
     */
    public Long lLeftPush( String key, String pivot, String value ) {
        return stringRedisTemplate.opsForList().leftPush( key, pivot, value );
    }

    /**
     * 从左边依次入栈
     */
    public Long lLeftPushAll( String key, String... values ) {
        return stringRedisTemplate.opsForList().leftPushAll( key, values );
    }

    /**
     * 从左边依次入栈 导入顺序按照 Collection 顺序 如: a b c => c b a
     */
    public Long lLeftPushAll( String key, Collection<String> values ) {
        return stringRedisTemplate.opsForList().leftPushAll( key, values );
    }

    /**
     * 指定 list 从左入栈，当 key 不存在时，什么也不做，返回 0 。
     *
     * @return 当前队列的长度
     */
    public Long lLeftPushIfPresent( String key, String value ) {
        return stringRedisTemplate.opsForList().leftPushIfPresent( key, value );
    }

    /**
     * 指定 list 从左出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public String lLeftPop( String key ) {
        return stringRedisTemplate.opsForList().leftPop( key );
    }

    /**
     * 指定 list 从左出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public String lLeftPop( String key, Duration timeout ) {
        return stringRedisTemplate.opsForList().leftPop( key, timeout.toMillis(), TimeUnit.MILLISECONDS );
    }

    /**
     * 指定 list 从右入栈
     *
     * @return 当前队列的长度
     */
    public Long lRightPush( String key, String value ) {
        return stringRedisTemplate.opsForList().rightPush( key, value );
    }

    /**
     * <p>
     * 将值 value 插入到列表 key 当中，位于值 pivot 之后。<br/> 当 pivot 不存在于列表 key 时，不执行任何操作。<br/> 当 key 不存在时， key 被视为空列表，不执行任何操作。<br/> 如果 key
     * 不是列表类型，返回一个错误。
     * </p>
     *
     * @return 当前队列的长度
     */
    public Long lRightPush( String key, String pivot, String value ) {
        return stringRedisTemplate.opsForList().rightPush( key, pivot, value );
    }

    /**
     * 指定 list 从右入栈，当 key 不存在时，什么也不做，返回 0 。
     *
     * @return 当前队列的长度
     */
    public Long lRightPushIfPresent( String key, String value ) {
        return stringRedisTemplate.opsForList().rightPushIfPresent( key, value );
    }

    /**
     * 从右边依次入栈
     */
    public Long lRightPushAll( String key, String... values ) {
        return stringRedisTemplate.opsForList().rightPushAll( key, values );
    }

    /**
     * 从右边依次入栈 导入顺序按照 Collection 顺序 如: a b c => a b c
     */
    public Long lRightPushAll( String key, Collection<String> values ) {
        return stringRedisTemplate.opsForList().rightPushAll( key, values );
    }

    /**
     * 指定 list 从右出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public String lRightPop( String key ) {
        return stringRedisTemplate.opsForList().rightPop( key );
    }

    /**
     * 指定 list 从右出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public String lRightPop( String key, Duration timeout ) {
        return stringRedisTemplate.opsForList().rightPop( key, timeout.toMillis(), TimeUnit.MILLISECONDS );
    }

    /**
     * 根据下标获取值
     */
    public String lIndex( String key, long index ) {
        return stringRedisTemplate.opsForList().index( key, index );
    }

    /**
     * 将列表 key 下标为 index 的元素的值设置为 value 。<br/> 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回false。
     */
    public void lSet( String key, long index, String value ) {
        stringRedisTemplate.opsForList().set( key, index, value );
    }

    /**
     * 获取指定列表数量
     */
    public Long lSize( String key ) {
        return stringRedisTemplate.opsForList().size( key );
    }

    /**
     * 获取列表 指定下标内的所有值
     */
    public List<String> lRange( String key, long start, long end ) {
        return stringRedisTemplate.opsForList().range( key, start, end );
    }

    /**
     * 删除 key 中 值为 value 的 count 个数.
     *
     * @return 成功删除的个数
     */
    public Long lDelete( String key, long count, Object value ) {
        return stringRedisTemplate.opsForList().remove( key, count, value );
    }

    /**
     * 删除 列表 [start,end] 以外的所有元素
     */
    public void lTrim( String key, long start, long end ) {
        stringRedisTemplate.opsForList().trim( key, start, end );
    }

    /**
     * 将 sourceKey 右出栈,并左入栈到 destinationKey
     *
     * @param sourceKey      右出栈的列表
     * @param destinationKey 左入栈的列表
     *
     * @return 操作的值
     */
    public String lRightPopAndLeftPush( String sourceKey, String destinationKey ) {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush( sourceKey, destinationKey );
    }

    /**
     * 将 sourceKey 右出栈,并左入栈到 destinationKey，{@link #lRightPopAndLeftPush}的阻塞版本
     *
     * @param sourceKey      右出栈的列表
     * @param destinationKey 左入栈的列表
     * @param timeout        等待超时的时间
     *
     * @return 操作的值
     */
    public String lRightPopAndLeftPush( String sourceKey, String destinationKey, Duration timeout ) {
        return stringRedisTemplate.opsForList()
                                  .rightPopAndLeftPush( sourceKey, destinationKey, timeout.toMillis(), TimeUnit.MILLISECONDS );
    }

    // ---------------------- set操作 无序不重复集合 ---------------------

    /**
     * 添加 set 元素
     */
    public Long sAdd( String key, String... values ) {
        return stringRedisTemplate.opsForSet().add( key, values );
    }

    /**
     * 获取两个集合的差集
     */
    public Set<String> sDifference( String key, String otherkey ) {
        return stringRedisTemplate.opsForSet().difference( key, otherkey );
    }

    /**
     * 获取 key 和 集合  collections 中的 key 集合的差集
     */
    public Set<String> sDifference( String key, Collection<String> otherKeys ) {
        return stringRedisTemplate.opsForSet().difference( key, otherKeys );
    }

    /**
     * 将  key 与 otherkey 的差集 ,添加到新的 destKey 集合中
     *
     * @return 返回差集的数量
     */
    public Long sDifferenceAndStore( String key, String otherkey, String destKey ) {
        return stringRedisTemplate.opsForSet().differenceAndStore( key, otherkey, destKey );
    }

    /**
     * 将 key 和 集合  collections 中的 key 集合的差集 添加到  destKey 集合中
     *
     * @return 返回差集的数量
     */
    public Long sDifferenceAndStore( String key, Collection<String> otherKeys, String destKey ) {
        return stringRedisTemplate.opsForSet().differenceAndStore( key, otherKeys, destKey );
    }

    /**
     * 删除一个或多个集合中的指定值
     *
     * @return 成功删除数量
     */
    public Long sRemove( String key, Object... values ) {
        return stringRedisTemplate.opsForSet().remove( key, values );
    }

    /**
     * 随机移除一个元素,并返回出来
     */
    public String sRandomPop( String key ) {
        return stringRedisTemplate.opsForSet().pop( key );
    }

    /**
     * 随机移除 count 个元素,并返回出来
     */
    public List<String> sRandomPop( String key, long count ) {
        return stringRedisTemplate.opsForSet().pop( key, count );
    }

    /**
     * 随机获取一个元素
     */
    public String sRandom( String key ) {
        return stringRedisTemplate.opsForSet().randomMember( key );
    }

    /**
     * 随机获取指定数量的元素,同一个元素可能会选中两次
     */
    public List<String> sRandom( String key, long count ) {
        return stringRedisTemplate.opsForSet().randomMembers( key, count );
    }

    /**
     * 随机获取指定数量的元素,去重(同一个元素只能选择两一次)
     */
    public Set<String> sRandomDistinct( String key, long count ) {
        return stringRedisTemplate.opsForSet().distinctRandomMembers( key, count );
    }

    /**
     * 将 key 中的 value 转入到 destKey 中
     *
     * @return 返回成功与否
     */
    public Boolean sMove( String key, String value, String destKey ) {
        return stringRedisTemplate.opsForSet().move( key, value, destKey );
    }

    /**
     * 集合的数量。当集合 key 不存在时，返回 0 。
     */
    public Long sSize( String key ) {
        return stringRedisTemplate.opsForSet().size( key );
    }

    /**
     * 判断 set 集合中 是否有 value
     */
    public Boolean sIsMember( String key, String value ) {
        return stringRedisTemplate.opsForSet().isMember( key, value );
    }

    /**
     * 返回 key 和 othere 的并集
     */
    public Set<String> sUnion( String key, String otherKey ) {
        return stringRedisTemplate.opsForSet().union( key, otherKey );
    }

    /**
     * 返回 key 和 otherKeys 的并集
     *
     * @param otherKeys key 的集合
     */
    public Set<String> sUnion( String key, Collection<String> otherKeys ) {
        return stringRedisTemplate.opsForSet().union( key, otherKeys );
    }

    /**
     * 将 key 与 otherKey 的并集,保存到 destKey 中
     *
     * @return destKey 数量
     */
    public Long sUnionAndStore( String key, String otherKey, String destKey ) {
        return stringRedisTemplate.opsForSet().unionAndStore( key, otherKey, destKey );
    }

    /**
     * 将 key 与 otherKey 的并集,保存到 destKey 中
     *
     * @return destKey 数量
     */
    public Long sUnionAndStore( String key, Collection<String> otherKeys, String destKey ) {
        return stringRedisTemplate.opsForSet().unionAndStore( key, otherKeys, destKey );
    }

    /**
     * 返回 key 和 otherKeys 的交集
     */
    public Set<String> sIntersect( String key, String otherKey ) {
        return stringRedisTemplate.opsForSet().intersect( key, otherKey );
    }

    /**
     * 返回 key 和 otherKeys 的交集
     *
     * @param otherKeys key 的集合
     */
    public Set<String> sIntersect( String key, Collection<String> otherKeys ) {
        return stringRedisTemplate.opsForSet().intersect( key, otherKeys );
    }

    /**
     * 将 key 与 otherKey 的交集,保存到 destKey 中
     *
     * @return destKey 数量
     */
    public Long sIntersectAndStore( String key, String otherKey, String destKey ) {
        return stringRedisTemplate.opsForSet().intersectAndStore( key, otherKey, destKey );
    }

    /**
     * 将 key 与 otherKey 的交集,保存到 destKey 中
     *
     * @return destKey 数量
     */
    public Long sIntersectAndStore( String key, Collection<String> otherKeys, String destKey ) {
        return stringRedisTemplate.opsForSet().intersectAndStore( key, otherKeys, destKey );
    }

    /**
     * 返回集合中所有元素
     */
    public Set<String> sMembers( String key ) {
        return stringRedisTemplate.opsForSet().members( key );
    }

    /**
     * 增量迭代返回集合中所有元素
     */
    public Cursor<String> sScan( String key, ScanOptions options ) {
        return stringRedisTemplate.opsForSet().scan( key, options );
    }

    // ---------------------- zset操作 根据 socre 排序 ---------------------

    /**
     * 添加 ZSet 元素
     */
    public Boolean zAdd( String key, String value, double score ) {
        return stringRedisTemplate.opsForZSet().add( key, value, score );
    }

    /**
     * 批量添加 ZSet 元素 <br/>
     * <code>
     * Set tupless = new HashSet<>(); <br/> ZSetOperations.TypedTuple objectTypedTuple1 = new DefaultTypedTuple<>("zset-5",9.6);
     * <br/> tupless.add(objectTypedTuple1); <br/> ZSetOperations.TypedTuple objectTypedTuple2 = new
     * DefaultTypedTuple<>("zset-6",9.5); <br/> tupless.add(objectTypedTuple2);
     * </code>
     */
    public Long zAddAll( String key, Set<ZSetOperations.TypedTuple<String>> tuples ) {
        return stringRedisTemplate.opsForZSet().add( key, tuples );
    }

    /**
     * Zset 删除一个或多个元素
     */
    public Long zRemove( String key, Object... values ) {
        return stringRedisTemplate.opsForZSet().remove( key, values );
    }

    /**
     * 对指定的 zset 的 value 值 , socre 属性做增减操作
     */
    public Double zIncrementScore( String key, String value, double score ) {
        return stringRedisTemplate.opsForZSet().incrementScore( key, value, score );
    }

    /**
     * 获取 key 中指定 value 的排名(从0开始,从小到大排序)
     */
    public Long zRank( String key, Object value ) {
        return stringRedisTemplate.opsForZSet().rank( key, value );
    }

    /**
     * 获取 key 中指定 value 的排名(从0开始,从大到小排序)
     */
    public Long zReverseRank( String key, Object value ) {
        return stringRedisTemplate.opsForZSet().reverseRank( key, value );
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从小到大,只有列名)
     */
    public Set<String> zRange( String key, long start, long end ) {
        return stringRedisTemplate.opsForZSet().range( key, start, end );
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从小到大,带上分数)
     */
    public Set<ZSetOperations.TypedTuple<String>> zRangeWithScores( String key, long start, long end ) {
        return stringRedisTemplate.opsForZSet().rangeWithScores( key, start, end );
    }

    /**
     * 返回从小到大分数范围内的元素不带分数的集合
     */
    public Set<String> zRangeByScore( String key, double min, double max ) {
        return stringRedisTemplate.opsForZSet().rangeByScore( key, min, max );
    }

    /**
     * 返回从小到大分数范围内 指定 count 数量的元素不带分数的集合
     */
    public Set<String> zRangeByScore( String key, double min, double max, long offset, long count ) {
        return stringRedisTemplate.opsForZSet().rangeByScore( key, min, max, offset, count );
    }

    /**
     * 获取从小到大分数范围内的 [min,max] 的排序结果集合
     */
    public Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores( String key, double min, double max ) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores( key, min, max );
    }

    /**
     * 返回从小到大分数范围内 指定 count 数量的元素集合
     */
    public Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores( String key, double min, double max, long offset,
																		   long count ) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores( key, min, max, offset, count );
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从大到小,只有列名)
     */
    public Set<String> zReverseRange( String key, long start, long end ) {
        return stringRedisTemplate.opsForZSet().reverseRange( key, start, end );
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从大到小,集合不带分数)
     */
    public Set<String> zReverseRangeByScore( String key, double min, double max ) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore( key, min, max );
    }

    /**
     * 返回 分数范围内 指定 count 数量的元素集合, 并且从 offset 下标开始(从大到小,不带分数的集合)
     */
    public Set<String> zReverseRangeByScore( String key, double min, double max, long offset, long count ) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore( key, min, max, offset, count );
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从大到小,带上分数)
     */
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeWithScores( String key, long start, long end ) {
        return stringRedisTemplate.opsForZSet().reverseRangeWithScores( key, start, end );
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从大到小,集合带分数)
     */
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeByScoreWithScores( String key, double min, double max ) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores( key, min, max );
    }

    /**
     * 返回 分数范围内 指定 count 数量的元素集合, 并且从 offset 下标开始(从大到小,带分数的集合)
     */
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeByScoreWithScores( String key, double min, double max,
																				  long offset, long count ) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores( key, min, max, offset, count );
    }

    /**
     * 返回指定分数区间 [min,max] 的元素个数
     */
    public Long zCount( String key, double min, double max ) {
        return stringRedisTemplate.opsForZSet().count( key, min, max );
    }

    /**
     * 返回 zset 集合数量
     */
    public Long zSize( String key ) {
        return stringRedisTemplate.opsForZSet().size( key );
    }

    /**
     * 获取指定成员的 score 值
     */
    public Double zScore( String key, Object value ) {
        return stringRedisTemplate.opsForZSet().score( key, value );
    }

    /**
     * 删除指定索引位置的成员,其中成员分数按( 从小到大 )
     */
    public Long zRemoveRange( String key, long start, long end ) {
        return stringRedisTemplate.opsForZSet().removeRange( key, start, end );
    }

    /**
     * 删除指定 分数范围 内的成员 [main,max],其中成员分数按( 从小到大 )
     */
    public Long zRemoveRangeByScore( String key, double min, double max ) {
        return stringRedisTemplate.opsForZSet().removeRangeByScore( key, min, max );
    }

    /**
     * key 和 other 两个集合的并集,保存在 destKey 集合中, 列名相同的 score 相加
     */
    public Long zUnionAndStore( String key, String otherKey, String destKey ) {
        return stringRedisTemplate.opsForZSet().unionAndStore( key, otherKey, destKey );
    }

    /**
     * key 和 otherKeys 多个集合的并集,保存在 destKey 集合中, 列名相同的 score 相加
     */
    public Long zUnionAndStore( String key, Collection<String> otherKeys, String destKey ) {
        return stringRedisTemplate.opsForZSet().unionAndStore( key, otherKeys, destKey );
    }

    /**
     * key 和 otherKeys 多个集合的并集,保存在 destKey 集合中, 列名相同的 score 相加
     */
    public Long zUnionAndStore( String key, Collection<String> otherKeys, String destKey,
								RedisZSetCommands.Aggregate aggregate ) {
        return stringRedisTemplate.opsForZSet().unionAndStore( key, otherKeys, destKey, aggregate );
    }

    /**
     * key 和 otherKeys 多个集合的并集,保存在 destKey 集合中, 列名相同的 score 相加
     */
    public Long zUnionAndStore( String key, Collection<String> otherKeys, String destKey, RedisZSetCommands.Aggregate aggregate
			, RedisZSetCommands.Weights weights ) {
        return stringRedisTemplate.opsForZSet().unionAndStore( key, otherKeys, destKey, aggregate, weights );
    }

    /**
     * key 和 otherKey 两个集合的交集,保存在 destKey 集合中
     */
    public Long zIntersectAndStore( String key, String otherKey, String destKey ) {
        return stringRedisTemplate.opsForZSet().intersectAndStore( key, otherKey, destKey );
    }

    /**
     * key 和 otherKeys 多个集合的交集,保存在 destKey 集合中
     */
    public Long zIntersectAndStore( String key, Collection<String> otherKeys, String destKey ) {
        return stringRedisTemplate.opsForZSet().intersectAndStore( key, otherKeys, destKey );
    }

    /**
     * key 和 otherKeys 多个集合的交集,保存在 destKey 集合中
     */
    public Long zIntersectAndStore( String key, Collection<String> otherKeys, String destKey, RedisZSetCommands.Aggregate aggregate ) {
        return stringRedisTemplate.opsForZSet().intersectAndStore( key, otherKeys, destKey, aggregate );
    }

    /**
     * key 和 otherKeys 多个集合的交集,保存在 destKey 集合中
     */
    public Long zIntersectAndStore( String key, Collection<String> otherKeys, String destKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights ) {
        return stringRedisTemplate.opsForZSet().intersectAndStore( key, otherKeys, destKey, aggregate, weights );
    }

    public boolean lock( EnumLock mode, String userId, String value, int timeOut ) {
        return this.strSetIfAbsent( Constants.SESSION_CLICK_LOCK.concat( mode.getKey() )
                                                                .concat( userId ), value, Duration.ofSeconds( timeOut ) );
    }


    public void unLock( EnumLock mode, String userId ) {
        this.unlink( Constants.SESSION_CLICK_LOCK.concat( mode.getKey() ).concat( userId ) );
    }

    public RedisConnectionFactory getConnectionFactory() {
        return stringRedisTemplate.getConnectionFactory();
    }


    /**
     * 分布式锁
     *
     * @param lockKey 锁key
     * @param timeOut 时间秒
     */
    public boolean adminLock( EnumLock mode, String lockKey, int timeOut ) {
        try {
            Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent( Constants.ADMIN_LOCK.concat( mode.getKey() )
                                                                                              .concat( lockKey ), "0", Duration.ofSeconds( timeOut ) );
            if ( lock == null ) {
                return false;
            }
            return lock;
        } catch ( Exception e ) {
            log.error( "admin加锁失败lockKey:{}", lockKey, e );
            return false;
        }

    }

    /**
     * 分布式锁
     *
     * @param lockKey 锁key
     */
    public boolean adminLock( EnumLock mode, String lockKey ) {
        return this.adminLock( mode, lockKey, 20 );
    }

    /**
     * 分布式锁
     *
     * @param lockKey 锁key
     * @param timeOut 时间秒
     */
    public boolean lock( String lockKey, int timeOut ) {
        try {

            Boolean lock = stringRedisTemplate.opsForValue()
                                              .setIfAbsent( Constants.LIVE_HOST_LOCK.concat( lockKey ), "0", Duration.ofSeconds( timeOut ) );
            if ( lock == null ) {
                return false;
            }
            return lock;
        } catch ( Exception e ) {
            log.error( "live加锁失败lockKey:{}", lockKey, e );
            return false;
        }

    }
}
