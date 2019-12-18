#!/usr/bin/env node

const readline = require('readline')

const readData = async () => {
    const rl = readline.createInterface({
        input: process.stdin,
    });

    const points = [];
    const pattern = /\s*(?<x0>-?\d*\.\d+)\s+(?<x1>-?\d*\.\d+)\s+(?<y>-?\d*\.\d+)\s*/

    for await (const line of rl) {
        const m = line.match(pattern)
        points.push({...m.groups})
    }

    return points
}

const sigmoid = (x, a) => {
    return 1 / (1 + Math.exp(-(x-a)))
}

const adaBoostXY = (points, {
    steps0 = 10,
    steps1 = 10,
    classifierCount = 100
}) => {
    const minX0 = Math.min(...points.map(p => p.x0)) - 1
    const maxX0 = Math.max(...points.map(p => p.x0)) + 1
    const minX1 = Math.min(...points.map(p => p.x1)) - 1
    const maxX1 = Math.max(...points.map(p => p.x1)) + 1

    // console.log('minX0', minX0)
    // console.log('maxX0', maxX0)
    // console.log('minX1', minX1)
    // console.log('maxX1', maxX1)

    const rangeX0 = maxX0 - minX0
    const rangeX1 = maxX1 - minX1

    const stepX0 = rangeX0 / steps0
    const stepX1 = rangeX1 / steps1


    // console.log('rangeX0', rangeX0)
    // console.log('rangeX1', rangeX1)
    // console.log('stepX0', stepX0)
    // console.log('stepX1', stepX1)

    const x0Classifiers = Array(steps0).fill(null).map(
        (_,i) => {
            return {
                fn: (p) => 2*Math.round(sigmoid(p.x0, (minX0 + i * stepX0))) - 1,
                cutoff: (minX0 + i * stepX0),
                feature: 'x0',
            }
        }
    )

    const x1Classifiers = Array(steps1).fill(null).map(
        (_,i) => {
            return {
                fn: (p) => 2*Math.round(sigmoid(p.x1, (minX1 + i * stepX1))) - 1,
                cutoff: (minX1 + i * stepX1),
                feature: 'x1',
            }
        }
    )

    const classifiers = [
        ...x1Classifiers,
        ...x0Classifiers,
    ].map(({fn, ...rest}) => {
        let error = 0;

        for (const p of points) {
            error += (fn(p) == p.y ? 0 : 1)
        }

        error /= points.length

        if(error > 0.5) {
            return {
                ...rest,
                inverted: true,
                fn: (p) => -1 * fn(p),
            }
        } else {
            return {
                ...rest,
                inverted: false,
                fn: fn,
            }
        }
    })

    for (p of points) {
        p.weight = 1 / points.length;
    }

    const selectedClassifiers = []

    while(selectedClassifiers.length < classifierCount) {
        let bestClassifier = null;
        let bestError = Infinity;
        let u = 0;
        let uu = null;
        for (const classifier of classifiers) {
            u++;
            let error = 0;

            for (const p of points) {
                error += p.weight * (classifier.fn(p) == p.y ? 0 : 1)
            }

            error /= points.length

            if(bestError > error) {
                bestError = error
                bestClassifier = classifier
                uu = u;
            }
        }

        alpha = 0.5 * Math.log((1 - bestError) / bestError)

        selectedClassifiers.push({
            alpha: alpha,
            ...bestClassifier,
        })


        let weightSum = 0;
        for (const p of points) {
            p.weight *= Math.exp(-1 * alpha * p.y * bestClassifier.fn(p))
            weightSum += p.weight
        }

        for (const p of points) {
            p.weight /= weightSum
        }
    }

    return selectedClassifiers
}


readData().then((points) => {
    const classifiers = adaBoostXY(points, {
        steps0: 300,
        steps1: 300,
        classifierCount: 50,
    })

    const totalClassifier = (p) => {
        result = 0;
        for(const c of classifiers) {
            result += c.alpha * c.fn(p)
        }

        return Math.sign(result);
    }

    let model = [];
    for(const c of classifiers) {
        model.push(`(${c.alpha} * {${c.feature} ${c.inverted ? '<' : '>'} ${c.cutoff}})`)
    }

    console.log(model.join(" + \n"))

    let correct = 0;

    for (p of points) {
        if(totalClassifier(p) == p.y) {
            correct++
        }
        console.log(p.x0, p.x1, totalClassifier(p), Math.round(p.y), (Math.round(p.y) * 2) + totalClassifier(p))
    }

    console.log(`${correct}/${points.length} correctly classified`)
})
