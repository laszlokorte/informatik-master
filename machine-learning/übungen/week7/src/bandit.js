const actions = 10
const plays = 1000
const repeats = 2000
const epsilons = [0.1,0.01,0]

// https://www.taygeta.com/random/gaussian.html
const normalDistribution = (mean, sigma) => {
    let y1 = 0;
    let y2 = 0;
    let returned = false
    return () => {
        if(!returned) {
            let w, x1, x2
            do {
                x1 = 2 * Math.random() - 1
                x2 = 2 * Math.random() - 1
                w = x1*x1 + x2*x2
            } while(w >= 1)
            w = Math.sqrt(-2 * Math.log(w) / 2)
            y1 = x1 * w
            y2 = x2 * w
            returned = true
            return y1 * sigma + mean
        } else {
            returned = false

            return y2 * sigma + mean
        }
    }
}

const meanDist = normalDistribution(0, 1)

const findHighest = (array, prop) => {
    let best = 0;
    for(let a = 0; a < array.length; a++) {
        if(array[a][prop] > array[best][prop]) {
            best = a
        }
    }

    return best
}

for(let e=0;e<epsilons.length;e++) {
    const eps = epsilons[e]

    let rewardSums = Array(plays).fill(0)
    let bestSelection = Array(plays).fill(0)

    for(let r=0;r<repeats;r++) {
        const arms = Array(actions).fill(0).map((_,i) => {
            const armMean = meanDist()
            return {
                name: "arm " + i,
                mean: armMean,
                sample: normalDistribution(armMean, 1),
                N: 0,
                Q: 0,
            }
        })

        const bestArm = findHighest(arms, 'mean')


        for(let p=0;p<plays;p++) {
            const actionIndex = Math.random() <= eps
                ? Math.floor(Math.random() * arms.length)
                : findHighest(arms, 'Q')

            const reward = arms[actionIndex].sample()
            arms[actionIndex].N += 1
            arms[actionIndex].Q += (reward - arms[actionIndex].Q) / arms[actionIndex].N
            rewardSums[p] += reward / repeats

            if(actionIndex === bestArm) {
                bestSelection[p] += 1/repeats
            }
        }
    }

    console.log("# epsilon", eps)
    rewardSums.forEach((r,i) => {
        console.log(i,r)
    })
    console.log("")
    bestSelection.forEach((r,i) => {
        console.log(i,r)
    })
    console.log("")
}
