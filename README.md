# Filter Library [ ![Download](https://api.bintray.com/packages/omaflak/maven/filter/images/download.svg) ](https://bintray.com/omaflak/maven/filter/_latestVersion)

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

    public User(int age, String firstName, String lastName) {
        this.age = age;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
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

 # Difference with Java 8 Stream
 
 Unlike Stream from Java 8, this library will return a list of **references** and will not waste memory. If you need a copy, just add the method `copy()` to the filter.
 
The library also include a method called `postOperation` which executes commands on the user(s) that respect your filter. For example, if you want to search the user which `id` is 42 and change its name to "answer" you would do it just like this :

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
 
 # Gradle

```Gradle
implementation 'me.aflak.libraries:filter-annotation:1.0'
annotationProcessor 'me.aflak.libraries:filter-processor:1.0'
```
 
 Many other functionnalities are available. I'll let you see for youreself !
 
 Any feedback would be greatly appreciated !
