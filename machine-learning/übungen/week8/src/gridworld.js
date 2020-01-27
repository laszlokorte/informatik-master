const V = [
    [0,0,0,0],
    [0,0,0,0],
    [0,0,0,0],
    [0,0,0,0],
]

const endStates = [[0,0],[3,3]]

const actions = [
    [-1,0],
    [0,-1],
    [1,0],
    [0,1],
]

deltaEps = 0.01


let delta = 0

do {
    delta = 0

    for(let x=0;x<V.length;x++) {
        loop: for(let y=0;y<V[x].length;y++) {
            for(let e=0;e<endStates.length;e++) {
                if(endStates[e][0] === x && endStates[e][1] === y) {
                    continue loop;
                }
            }
            const v = V[x][y]
            let sum = 0;
            for(let a=0;a<actions.length;a++) {
                const action = actions[a]
                let xx = x + action[0]
                let yy = y + action[1]
                let xxx = (xx < V.length && xx >= 0) ? xx : x
                let yyy = (yy < V[xxx].length && yy >= 0) ? yy : y
                sum += 0.25 * (-1 + V[xxx][yyy])
            }
            V[x][y] = sum;
            delta = Math.max(delta, Math.abs(sum - v))
        }
    }
} while (delta > deltaEps)


for(let x=0;x<4;x++) {
    const row = []
    for(let y=0;y<4;y++) {
        let actionCombi = 0;
        let bestAction = null;
        for(let a=0;a<actions.length;a++) {
            const dx = actions[a][0]
            const dy = actions[a][1]
            const tx = x + dx
            const ty = y + dy
            if(tx >= 0 && ty >= 0 && tx < V.length && ty < V[tx].length) {

                if(0 === V[x][y]) {
                    continue
                }

                if(bestAction === null) {
                    bestAction = a
                    actionCombi |= Math.pow(2,a)
                    continue
                }
                const besttx = x + actions[bestAction][0]
                const bestty = y + actions[bestAction][1]

                if(V[besttx][bestty] < V[tx][ty]) {
                    bestAction = a
                    actionCombi = Math.pow(2,a)
                } else if(Math.abs(V[besttx][bestty] / V[tx][ty] - 1) < deltaEps) {
                    actionCombi |= Math.pow(2,a)
                }
            }
        }

        row.push(actionCombi)
    }
    console.log(row.map(r => [
        '-',
        'up',
        'left',
        'up/left',
        'down',
        'up/down',
        'left/down',
        'up/left/down',
        'right',
        'up/right',
        'left/right',
        'up/left/right',
        'down/right',
        'up/down/right',
        'left/down/right',
        'up/left/down/right',
    ][r]).map(d => '['+d+']').join(", "))
}

    console.log(V.map((r) => r.map(v => Math.round(v*100.) / 100.0)))
