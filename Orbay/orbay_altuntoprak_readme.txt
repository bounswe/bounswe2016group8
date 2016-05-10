The data set my project works on is Popes.

So we have their Papal names, ages of death and their ordinals(e.g Pope Francis is 266th Pope).

If the user inputs a name that is not a Pope's Papal name, the application sorts the popes according to the dates they started being popes. Then displays them.

If the user inputs a Papal name, I made a sort of metric* between popes. The application sorts the popes according to their distance to the given name.

"The metric": Distance between two popes P and Q is defined to be

0 <----> P and Q are the same pope.
1000 + 3 * abs( P's Ordinal - Q's Ordinal ) + abs( P's age of death -  Q's_age_of_death );