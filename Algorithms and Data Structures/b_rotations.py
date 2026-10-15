import sys

sys.setrecursionlimit(10000)


class Node:
    __slots__ = ('key', 'left', 'right')

    def __init__(self, key):
        self.key = key
        self.left = self.right = None


def add(v, x):
    if v is None:
        return Node(x)
    if x < v.key:
        v.left = add(v.left, x)
    elif x > v.key:
        v.right = add(v.right, x)
    else:
        return v
    return v


def rotate_left(a):
    b = a.right
    a.right = b.left
    b.left = a
    return b


def rotate_right(a):
    b = a.left
    a.left = b.right
    b.right = a
    return b


def apply_rotation(v, x, direction):
    if x < v.key:
        v.left = apply_rotation(v.left, x, direction)
    elif x > v.key:
        v.right = apply_rotation(v.right, x, direction)
    elif direction == b'left':
        return rotate_left(v)
    else:
        return rotate_right(v)
    return v


def preorder(root):
    if root is None:
        return 'Empty'
    result = []
    stack = [root]
    while stack:
        v = stack.pop()
        result.append(str(v.key))
        if v.right is not None:
            stack.append(v.right)
        if v.left is not None:
            stack.append(v.left)
    return ' '.join(result)


def main():
    tokens = iter(sys.stdin.buffer.read().split())
    n = int(next(tokens))
    root = None
    for _ in range(n):
        root = add(root, int(next(tokens)))
    m = int(next(tokens))
    answer = []
    for _ in range(m):
        direction = next(tokens)
        x = int(next(tokens))
        root = apply_rotation(root, x, direction)
        answer.append(preorder(root))
    sys.stdout.write('\n'.join(answer))


if __name__ == '__main__':
    main()
