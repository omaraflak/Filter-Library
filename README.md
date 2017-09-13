# Filter Library [ ![Download](https://api.bintray.com/packages/omaflak/maven/filter-annotation/images/download.svg) ](https://bintray.com/omaflak/maven/filter-annotation/_latestVersion)

Android library to filter any object in a list using a simple annotation.

The library comes up with various handy methods to filter your objects. All you have to do is add an annotation !

# @Filterable

Create a class, **generate the getters**, and set the annotation `@Filterable`.

```Java
@Filterable
public class User {
    private int age;
    private String firstName;
    private String lastName;

    public int getAge() { return age; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
```

That's all you need to do !

Rebuild your project, and the annotation processor should have generated a class called `UserFilter` which has methods with names that matches the class attributes (i.e. age(), firstName(), lastName()) :

```Java
List<User> users = getUserList();

List<User> result = UserFilter.builder()
        .age().greaterThan(10)
        .firstName().startsWith("A")
        .on(users);
```

## Double filter

If your class contains an object as attribute, for instance :

```Java
@Filterable
public class User {
    private int age;
    private String name;
    private Shape shape;

    public int getAge() { return age; }
    public String getName() { return name; }
    public Shape getShape() { return shape; }
```

Simply annotate the class Shape with `@Filterable` :

```Java
@Filterable
public class Shape {
    private float size;
    private float mass;

    public float getSize() { return size; }
    public float getMass() { return mass; }
}
```

Then filter the field `shape` with the function `matches()` :

```Java
List<User> result = UserFilter.builder()
        .age().smallerThan(50)
        .shape().matches(ShapeFilter.builder()
            .size().greaterThan(1.80)
            .build(), ShapeFilter.class)
        .on(users);
```

## Crazy condition

The library provides several methods to compare objects, including a `regex()` method. Still, if you can't formulate your condition, you can write it manually as follows :

```Java
List<User> result = UserFilter.builder()
        .extraCondition(new Condition<User>() {
            @Override
            public boolean verify(User object) {
                return someCrazyCondition(object);
            }
        })
        .on(users);
```

 # Difference with Java 8 Stream
 
Unlike Stream from Java 8, this library will return a list of **references** and will not waste memory. If you need a copy, just add the method `copy()` to the filter.
 
The library also includes a method called `postOperation` which executes commands on the user(s) that respect your filter. For example, if you want to search the user which `id` is 42 and change its name to "answer" you would do it like this :

```Java
UserFilter.builder()
        .id().equalsTo(42)
        .postOperation(new Operation<User>() {
            @Override
            public void execute(User object) {
                object.setName("Answer");
            }
        })
        .on(users);
 ```
 Note that you don't even have to store the result of the function.
 
 # Gradle

```Gradle
repositories {
    mavenLocal()
}

dependencies {
    annotationProcessor 'me.aflak.libraries:filter-processor:1.0'
    implementation 'me.aflak.libraries:filter-annotation:1.0'
}
```
 
Any feedback would be greatly appreciated !
