SmoothLife
==========
SmoothLife is an artificial life simulator. The artificial lives in this simulation are circular entities called blobs. A world consists of multiple species of blobs. Each blob has a brain (artificial neural network) that is responsible for determining the actions of the blob. The goal of the blob is to perform actions that prolongs it's life. The blobs that live longer are deemed more fit, and, as a result, their chromosomes get spread throughout the population.
The sole factor that determines how long a blob lives is it's energy level. If it's energy drops to zero or below, the blob is considered dead. With each world update blobs loose energy, and they have to find ways to replenish the lost energy. A blob that is marked as a predator can gain energy by attacking blobs marked as prey. Prey blobs can gain energy by grouping together with other prey blobs.

The actions a blob can make are very limited. A blob can turn left, turn right, move forward, and perform a special action. For predator blobs the special action is attack, for prey blobs the special action is regenerating energy from nearby prey blobs.

When a new world is created the different species are also created. Unless otherwise specified there is only two species. One species is the prey species and the other is the predator species. All blobs in the prey species are prey blobs and the same for the predator species. Predator blobs have a dark red dot in the center of their body and prey blobs just have slighter lighter color in their center. When a predator blob attacks it's dark red dot becomes brighter.

Each species has it's own gene pool that is populated by the blobs of the same species. When a blob dies, a genetic algorithm is run on the gene pool to create a new (and hopefully better) chromosome. The chromosome is used to determine the size, color of the blob. It is also used to determine the weights for blob's brain.
