local expire=tonumber(ARGV[2])
local ret=redis.call('set',KEYS[1],ARGV[1],'NX','PX',expire)
local strret=tostring(ret)
redis.call('set', 'result', strret)
if strret == 'false' then
    return false
else
    return true
end