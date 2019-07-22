# Dgraph4jDemo
java操作Dgraph例子
https://github.com/oguangxili/Dgraph4jDemo.git

说明:

Test类是官方给的hello world.包括建库,删除模式,设置模式,查询全套操作.

TestSimplify类是简化版本,在图库里面一次添加索引,一次数据突变后,即可自己查询

HelloWorld是自己实验的数据.


构建图库:
{
  set {
   _:luke <name> "Luke Skywalker" .
   _:leia <name> "Princess Leia" .
   _:han <name> "Han Solo" .
   _:lucas <name> "George Lucas" .
   _:irvin <name> "Irvin Kernshner" .
   _:richard <name> "Richard Marquand" .

   _:sw1 <name> "Star Wars: Episode IV - A New Hope" .
   _:sw1 <release_date> "1977-05-25" .
   _:sw1 <revenue> "775000000" .
   _:sw1 <running_time> "121" .
   _:sw1 <starring> _:luke .
   _:sw1 <starring> _:leia .
   _:sw1 <starring> _:han .
   _:sw1 <director> _:lucas .

   _:sw2 <name> "Star Wars: Episode V - The Empire Strikes Back" .
   _:sw2 <release_date> "1980-05-21" .
   _:sw2 <revenue> "534000000" .
   _:sw2 <running_time> "124" .
   _:sw2 <starring> _:luke .
   _:sw2 <starring> _:leia .
   _:sw2 <starring> _:han .
   _:sw2 <director> _:irvin .

   _:sw3 <name> "Star Wars: Episode VI - Return of the Jedi" .
   _:sw3 <release_date> "1983-05-25" .
   _:sw3 <revenue> "572000000" .
   _:sw3 <running_time> "131" .
   _:sw3 <starring> _:luke .
   _:sw3 <starring> _:leia .
   _:sw3 <starring> _:han .
   _:sw3 <director> _:richard .

   _:st1 <name> "Star Trek: The Motion Picture" .
   _:st1 <release_date> "1979-12-07" .
   _:st1 <revenue> "139000000" .
   _:st1 <running_time> "132" .
  }
}


构建json图库:
{"set":{
  "name": "diggy",
  "food": "pizza"
}}


增加模式(查询时设置查询字段为索引):
<director>: uid .
<name>: string @index(exact) .
<release_date>: datetime @index(year) .
<revenue>: float .
<running_time>: int .
<starring>: uid .

查询:
{
  me(func: has(starring)) @filter(ge(release_date, "1980")) {
    name
  }
}



ExportMutations类写了一个生成set语句的例子:
查询,需要在http://localhost:8000/?latest下面查询.

{
get(func: eq(name,"初一一班")) {
	    name,
		stuent{
			stuname,
		    sex,
		    birth,
	    }
	}
}